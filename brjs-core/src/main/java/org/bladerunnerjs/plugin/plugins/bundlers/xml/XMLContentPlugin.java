package org.bladerunnerjs.plugin.plugins.bundlers.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.html.HTMLAssetPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class XMLContentPlugin extends AbstractContentPlugin
{

	private ContentPathParser contentPathParser;
	private BRJS brjs = null;
	private AssetPlugin xmlAssetPlugin;
	
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder.accepts("bundle.xml").as("bundle-request");
		contentPathParser = contentPathParserBuilder.build();
	}

	@Override
	public void setBRJS(BRJS brjs) 
	{
		this.brjs  = brjs;
		xmlAssetPlugin = brjs.plugins().assetProducer(XMLAssetPlugin.class);
	}

	@Override
	public String getRequestPrefix() {
		return "bundle.xml";
	}
	
	@Override
	public String getGroupName() {
		return "application/xml";
	}
	
	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException
	{
		List<String> result = new ArrayList<String>();
		XmlBundlerConfig config = new XmlBundlerConfig(brjs);
		if(!config.isbundleConigAvailable()){
			return result;
		}
		
		try {
			result.add(contentPathParser.createRequest("bundle-request"));
		} catch (MalformedTokenException e) {
			throw new ContentProcessingException(e);
		}
		return result;
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException
	{
		return getValidDevContentPaths(bundleSet, locales);
	}

	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws ContentProcessingException
	{
		//TODO not parse the config on every execution
		XmlBundlerConfig config = new XmlBundlerConfig(brjs);
		if(!config.isbundleConigAvailable()){
			throw new ContentProcessingException("Cannot process XML no configuration file found");
		}
		XmlBundleWriter bundleWriter = new XmlBundleWriter(config);
		
		try{
			String outputEncoding = brjs.bladerunnerConf().getDefaultOutputEncoding();
			Writer output = new OutputStreamWriter(os, outputEncoding);
			List<Asset> xmlAssets = bundleSet.getResourceFiles(xmlAssetPlugin);
//			List<Asset> xmlAssets = bundleSet.getResourceFiles("xml");
			bundleWriter.writeBundle(xmlAssets, output);
			output.flush();
		}
		catch( IOException | ConfigException |  XMLStreamException  e) {
			throw new ContentProcessingException(e, "Error while processing XML assets '" );
		}	
	}
	
}
