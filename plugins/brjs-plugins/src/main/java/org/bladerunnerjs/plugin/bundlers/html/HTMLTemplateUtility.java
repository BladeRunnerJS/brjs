package org.bladerunnerjs.plugin.bundlers.html;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StreamedSource;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.model.exception.NamespaceException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.utility.RequirePathUtility;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.utility.AppMetadataUtility;

public class HTMLTemplateUtility {
	public static final String SCRIPT_TEMPLATE_WARNING = "A script tag was used for the '%s' template, but these are now deprecated in favor of template tags.";
	
	public static List<Reader> getReaders(BundleSet bundleSet, String version) throws ContentFileProcessingException, ContentProcessingException {
		Map<String, Asset> identifiers = new TreeMap<String, Asset>();
		List<Asset> htmlAssets = bundleSet.getAssets("html!");
		Logger logger = bundleSet.getBundlableNode().root().logger(HTMLTemplateUtility.class);
		
		List<Reader> readerList = new ArrayList<Reader>();
		for(Asset htmlAsset : htmlAssets){
			try {
				TemplateInfo templateInfo = getTemplateInfo(htmlAsset, identifiers, logger);

				try(Reader reader = htmlAsset.getReader()) {
					readerList.add(new StringReader("\n<!-- " + htmlAsset.getAssetName() + " -->\n"));

					String bundlePath = AppMetadataUtility.getRelativeVersionedBundlePath(bundleSet.getBundlableNode().app(), version, "").replaceFirst("/$", "");
					String xmlBundlePathToken = AppMetadataUtility.XML_BUNDLE_PATH_TOKEN;

					String htmlContent = IOUtils.toString(reader);
					String replaced =  htmlContent.replace(xmlBundlePathToken, bundlePath);
					
					if(templateInfo.requiresWrapping) {
						readerList.add(new StringReader("<template id='" + templateInfo.identifier + "' data-auto-wrapped='true'>\n"));
					}
					
					readerList.add(new StringReader(replaced));
					
					if(templateInfo.requiresWrapping) {
						readerList.add(new StringReader("</template>\n"));
					}
				}
			}
			catch (IOException | NamespaceException | RequirePathException e) {
				throw new ContentProcessingException(e, "Error while bundling asset '" + htmlAsset.getAssetPath() + "'.");
			}
		}
		
		return readerList;
	}
	
	private static TemplateInfo getTemplateInfo(Asset htmlAsset, Map<String, Asset> identifiers, Logger logger) throws IOException, ContentFileProcessingException, NamespaceException, RequirePathException
	{
		StartTag startTag = getStartTag(htmlAsset);
		String identifier = startTag.getAttributeValue("id");
		AssetContainer assetContainer = htmlAsset.assetContainer();
		
		if(startTag.getName().equals("script")) {
			logger.warn(SCRIPT_TEMPLATE_WARNING, identifier);
		}
		
		if(identifier == null)
		{
			String idMessage = (assetContainer.isNamespaceEnforced()) ?
				"a namespaced ID of '" +RequirePathUtility.calculateNamespace(assetContainer)+ ".*'" : "an ID";
			
			throw new NamespaceException( "HTML template found without an identifier: '" +
					startTag.toString() + "'.  Root element should have " + idMessage + ".");
		}
		
		RequirePathUtility.assertIdentifierCorrectlyNamespaced(htmlAsset, identifier);
		
		Asset assetWithDuplicateId = identifiers.get(identifier);
		if(assetWithDuplicateId == null){
			identifiers.put(identifier, htmlAsset);
		}else{
			throw new NamespaceException("HTML template found with a duplicate identifier: '" +
						identifier + "'. The same identifier is used for the file:\n'" 
						+ assetWithDuplicateId.getAssetPath()
						+ "'.");
		}
		
		return new TemplateInfo(identifier, !startTag.getName().equals("template"));
	}
	
	private static StartTag getStartTag(Asset htmlAsset) throws IOException
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
	
	private static class TemplateInfo {
		public final String identifier;
		public final boolean requiresWrapping;

		public TemplateInfo(String identifier, boolean requiresWrapping) {
			this.identifier = identifier;
			this.requiresWrapping = requiresWrapping;
		}
	}
}
