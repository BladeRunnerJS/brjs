package org.bladerunnerjs.plugin.plugins.bundlers.xml;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.plugin.CharResponseContent;
import org.bladerunnerjs.plugin.ResponseContent;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;
import org.bladerunnerjs.utility.AppMetadataUtility;


public class XMLContentPlugin extends AbstractContentPlugin
{
	
	private ContentPathParser contentPathParser;
	private BRJS brjs = null;
	private AssetPlugin xmlAssetPlugin;
	private final List<String> requestPaths = new ArrayList<>();
	private XmlBundlerConfig xmlBundlerConfig;
	
	{
		try {
			ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
			contentPathParserBuilder.accepts("xml/bundle.xml").as("bundle-request");
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
		this.brjs  = brjs;
		xmlAssetPlugin = brjs.plugins().assetPlugin(XMLAssetPlugin.class);
		xmlBundlerConfig = new XmlBundlerConfig(brjs);
	}

	@Override
	public String getRequestPrefix() {
		return "xml";
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
	public ResponseContent handleRequest(ParsedContentPath contentPath, BundleSet bundleSet, UrlContentAccessor output, String version) throws ContentProcessingException
	{
		XmlBundleWriter bundleWriter = new XmlBundleWriter(xmlBundlerConfig);
		List<Asset> xmlAssets = bundleSet.getResourceFiles(xmlAssetPlugin);

		try{
			StringWriter bufferedOutput = new StringWriter();
			
			if (xmlBundlerConfig.isbundleConfigAvailable()){
				bundleWriter.writeBundle(xmlAssets, bufferedOutput);
			} else {
				bundleWriter.concatenateBundle(xmlAssets, bufferedOutput);
			}
			
			String bundlePath = AppMetadataUtility.getVersionedBundlePath(version);
			String unversionedBundlePath = AppMetadataUtility.getUnversionedBundlePath();
			String xmlBundlePathToken = AppMetadataUtility.XML_BUNDLE_PATH_TOKEN;
			String xmlUnversionedBundlePathToken = AppMetadataUtility.XML_UNVERSIONED_BUNDLE_PATH_TOKEN;
			//TODO: Can we do a streaming replacement rather than buffer into  string?
			String result = bufferedOutput.toString().replace(xmlBundlePathToken, bundlePath).replace(xmlUnversionedBundlePathToken, unversionedBundlePath);
			
			return new CharResponseContent(brjs, result);
		}
		catch(    XMLStreamException  e) {
			throw new ContentProcessingException(e, "Error while processing XML assets '" );
		}
	}
	
	private List<String> getValidContentPaths(BundleSet bundleSet) throws ContentProcessingException {
		XmlBundlerConfig config = new XmlBundlerConfig(brjs);
		
		return (!config.isbundleConfigAvailable() || bundleSet.getResourceFiles(xmlAssetPlugin).isEmpty()) ? Collections.emptyList() : requestPaths;
	}
}
