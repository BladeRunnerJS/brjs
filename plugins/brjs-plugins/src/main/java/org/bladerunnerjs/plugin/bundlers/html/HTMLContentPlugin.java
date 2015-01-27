package org.bladerunnerjs.plugin.bundlers.html;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StreamedSource;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.aliasing.NamespaceException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.plugin.AssetPlugin;
import org.bladerunnerjs.api.plugin.CharResponseContent;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.utility.NamespaceUtility;
import org.bladerunnerjs.utility.AppMetadataUtility;


public class HTMLContentPlugin extends AbstractContentPlugin
{
	private Map<String, Asset> identifiers = new TreeMap<String, Asset>();
	private final List<String> requestPaths = new ArrayList<>();
	
	private AssetPlugin htmlAssetPlugin;
	private BRJS brjs;
	
	{
		requestPaths.add("html/bundle.html");
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
		htmlAssetPlugin = brjs.plugins().assetPlugin(HTMLAssetPlugin.class);
		this.brjs = brjs;
	}
	
	@Override
	public String getRequestPrefix() {
		return "html";
	}

	@Override
	public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException
	{
		return (bundleSet.getResourceFiles(htmlAssetPlugin).isEmpty()) ? Collections.emptyList() : requestPaths;
	}

	@Override
	public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor output, String version) throws MalformedRequestException, ContentProcessingException
	{
		if(!contentPath.equals(requestPaths.get(0))) {
			throw new MalformedRequestException(contentPath, "Requests must be for exactly '" + requestPaths.get(0) + "'.");
		}
		
		identifiers = new TreeMap<String, Asset>();
		List<Asset> htmlAssets = bundleSet.getResourceFiles(htmlAssetPlugin);
		
		List<Reader> readerList = new ArrayList<Reader>();
		for(Asset htmlAsset : htmlAssets){
			try {
				validateSourceHtml(htmlAsset);

				try(Reader reader = htmlAsset.getReader()) {
					readerList.add(new StringReader("\n<!-- " + htmlAsset.getAssetName() + " -->\n"));

					String bundlePath = AppMetadataUtility.getRelativeVersionedBundlePath(version, "").replaceFirst("/$", "");
					String xmlBundlePathToken = AppMetadataUtility.XML_BUNDLE_PATH_TOKEN;

					String htmlContent = IOUtils.toString(reader);
					String replaced =  htmlContent.replace(xmlBundlePathToken, bundlePath);
					readerList.add(new StringReader(replaced));
				}
			}
			catch (IOException | NamespaceException | RequirePathException e) {
				throw new ContentProcessingException(e, "Error while bundling asset '" + htmlAsset.getAssetPath() + "'.");
			}
		}
		
		return new CharResponseContent( brjs, readerList );		
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
