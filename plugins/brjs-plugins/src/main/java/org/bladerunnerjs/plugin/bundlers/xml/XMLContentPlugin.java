package org.bladerunnerjs.plugin.bundlers.xml;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.plugin.CharResponseContent;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.utility.AppMetadataUtility;

public class XMLContentPlugin extends AbstractContentPlugin
{
	private BRJS brjs = null;
	private final List<String> requestPaths = new ArrayList<>();
	private XmlBundlerConfig xmlBundlerConfig;
	
	{
		requestPaths.add("xml/bundle.xml");
	}

	@Override
	public void setBRJS(BRJS brjs) 
	{
		this.brjs  = brjs;
		xmlBundlerConfig = new XmlBundlerConfig(brjs);
	}

	@Override
	public String getRequestPrefix() {
		return "xml";
	}

	@Override
	public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException
	{
		return bundleSet.getAssets("xml!").isEmpty() ? Collections.emptyList() : requestPaths;
	}
	
	@Override
	public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor output, String version) throws MalformedRequestException, ContentProcessingException
	{
		if(!contentPath.equals(requestPaths.get(0))) {
			throw new MalformedRequestException(contentPath, "Requests must be for exactly '" + requestPaths.get(0) + "'.");
		}
		
		XmlBundleWriter bundleWriter = new XmlBundleWriter(xmlBundlerConfig);
		List<Asset> xmlAssets = bundleSet.getAssets("xml!");

		try{
			StringWriter bufferedOutput = new StringWriter();
			
			if (xmlBundlerConfig.isbundleConfigAvailable()){
				bundleWriter.writeBundle(xmlAssets, bufferedOutput);
			} else {
				bundleWriter.concatenateBundle(xmlAssets, bufferedOutput);
			}
			
			String bundlePath = AppMetadataUtility.getRelativeVersionedBundlePath(bundleSet.bundlableNode().app(), version, "").replaceFirst("/$", "");
			String xmlBundlePathToken = AppMetadataUtility.XML_BUNDLE_PATH_TOKEN;
			//TODO: Can we do a streaming replacement rather than buffer into  string?
			String result = bufferedOutput.toString().replace(xmlBundlePathToken, bundlePath);
			
			return new CharResponseContent(brjs, result);
		}
		catch(IOException | XMLStreamException  e) {
			throw new ContentProcessingException(e, "Error while processing XML assets '" );
		}
	}
}
