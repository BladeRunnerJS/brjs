package org.bladerunnerjs.plugin.plugins.bundlers.html;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StreamedSource;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ContentPluginOutput;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.commonjs.CommonJsSourceModule;
import org.bladerunnerjs.utility.AdhocTimer;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;
import org.bladerunnerjs.utility.NamespaceUtility;
import org.bladerunnerjs.utility.ServedAppMetadataUtility;

import com.Ostermiller.util.ConcatReader;


public class HTMLContentPlugin extends AbstractContentPlugin
{
	private ContentPathParser contentPathParser;
	private Map<String, Asset> identifiers = new TreeMap<String, Asset>();
	private final List<String> requestPaths = new ArrayList<>();
	
	private BRJS brjs;
	private AssetPlugin htmlAssetPlugin;
	
	{
		try {
			ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
			contentPathParserBuilder.accepts("html/bundle.html").as("bundle-request");
			contentPathParser = contentPathParserBuilder.build();
			requestPaths.add(contentPathParser.createRequest("bundle-request"));
		}
		catch(MalformedTokenException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
		htmlAssetPlugin = brjs.plugins().assetPlugin(HTMLAssetPlugin.class);
	}
	
	@Override
	public String getRequestPrefix() {
		return "html";
	}

	@Override
	public String getCompositeGroupName() {
		return null;
	}
	
	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException
	{
		return getValidContentPaths(bundleSet);
	}
	
	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException
	{
		return getValidContentPaths(bundleSet);
	}

	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, ContentPluginOutput output, String version) throws ContentProcessingException
	{
		identifiers = new TreeMap<String, Asset>();
		List<Asset> htmlAssets = bundleSet.getResourceFiles(htmlAssetPlugin);
		
		List<Reader> readerList = new ArrayList<Reader>();
		for(Asset htmlAsset : htmlAssets){
			try {
				validateSourceHtml(htmlAsset);

				try(Reader reader = htmlAsset.getReader()) {
					readerList.add(new StringReader("\n<!-- " + htmlAsset.getAssetName() + " -->\n"));

					String bundlePath = ServedAppMetadataUtility.getVersionedBundlePath(version);
					String unversionedBundlePath = ServedAppMetadataUtility.getUnversionedBundlePath();
					String xmlBundlePathToken = ServedAppMetadataUtility.XML_BUNDLE_PATH_TOKEN;
					String xmlUnversionedBundlePathToken = ServedAppMetadataUtility.XML_UNVERSIONED_BUNDLE_PATH_TOKEN;

					String htmlContent = IOUtils.toString(reader);
					String replaced =  htmlContent.replace(xmlBundlePathToken, bundlePath).replace(xmlUnversionedBundlePathToken, unversionedBundlePath);
					readerList.add(new StringReader(replaced));
				}
			}
			catch (IOException | NamespaceException | RequirePathException e) {
				throw new ContentProcessingException(e, "Error while bundling asset '" + htmlAsset.getAssetPath() + "'.");
			}
		}
		
		Reader[] readers = new Reader[readerList.size()];
		readerList.toArray(readers);
		output.setReader(new ConcatReader(readers));
		
	}
	
	private List<String> getValidContentPaths(BundleSet bundleSet) {
		return (bundleSet.getResourceFiles(htmlAssetPlugin).isEmpty()) ? Collections.emptyList() : requestPaths;
	}
	
	private void validateSourceHtml(Asset htmlAsset) throws IOException, ContentFileProcessingException, NamespaceException, RequirePathException
	{
		StartTag startTag = getStartTag(htmlAsset);
		String identifier = startTag.getAttributeValue("id");
		
		if(identifier == null)
		{
			String idMessage = (htmlAsset.assetLocation().assetContainer().isNamespaceEnforced()) ?
				"a namespaced ID of '" + NamespaceUtility.convertToNamespace(htmlAsset.assetLocation().requirePrefix()) + ".*'" : "an ID";
			
			throw new NamespaceException( "HTML template found without an identifier: '" +
					startTag.toString() + "'.  Root element should have " + idMessage + ".");
		}
		
		htmlAsset.assetLocation().assertIdentifierCorrectlyNamespaced(identifier);
		
		Asset assetWithDuplicateId = identifiers.get(identifier);
		if(assetWithDuplicateId == null){
			identifiers.put(identifier, htmlAsset);
		}else{
			throw new NamespaceException("HTML template found with a duplicate identifier: '" +
						identifier + "'. The same identifier is used for the file:\n'" 
						+ assetWithDuplicateId.getAssetPath()
						+ "'.");
		}
	}
	
	
	private StartTag getStartTag(Asset htmlAsset) throws IOException
	{
		try(Reader reader = htmlAsset.getReader())
		{
			StreamedSource streamedSource = new StreamedSource(reader);
			StartTag startTag = null;
			
			try
			{
				for(Segment nextSegment : streamedSource)
				{
					if(nextSegment instanceof StartTag)
					{
						startTag = (StartTag) nextSegment;
						break;
					}
				}
			}
			finally
			{
				streamedSource.close();
			}
			
			return startTag;
		}
	}
	
}
