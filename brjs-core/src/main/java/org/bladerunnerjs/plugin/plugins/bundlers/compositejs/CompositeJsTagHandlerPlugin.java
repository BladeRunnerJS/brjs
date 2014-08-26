package org.bladerunnerjs.plugin.plugins.bundlers.compositejs;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;

public class CompositeJsTagHandlerPlugin extends AbstractTagHandlerPlugin {
	private BRJS brjs;
	private ContentPlugin compositeJsBundlerPlugin;
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
		compositeJsBundlerPlugin = brjs.plugins().contentPlugin("js");
	}
	
	@Override
	public String getTagName() {
		return "js.bundle";
	}
	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, Writer writer, String version) throws IOException {
		writeTagContent(tagAttributes, RequestMode.Dev, bundleSet, locale, writer, version);
	}
	
	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, Writer writer, String version) throws IOException {
		writeTagContent(tagAttributes, RequestMode.Prod, bundleSet, locale, writer, version);
	}	
	
	@Override
	public List<String> getGeneratedRequests(RequestMode requestMode, Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, String version) throws MalformedTokenException, ContentProcessingException
	{
		boolean isDev = requestMode == RequestMode.Dev;
		List<String> possibleRequests = new ArrayList<String>();
		MinifierSetting minifierSettings = new MinifierSetting(tagAttributes);
		String minifierSetting = (isDev) ? minifierSettings.devSetting() : minifierSettings.prodSetting();
		
		if(minifierSetting.equals(MinifierSetting.SEPARATE_JS_FILES)) {
			for(ContentPlugin contentPlugin : brjs.plugins().contentPlugins("text/javascript")) {
				List<String> contentPaths = (isDev) ? contentPlugin.getValidDevContentPaths(bundleSet) : contentPlugin.getValidProdContentPaths(bundleSet);
				possibleRequests.addAll(contentPaths);
			}
		}
		else {
			String bundleRequestForm = (isDev) ? "dev-bundle-request" : "prod-bundle-request";
			possibleRequests.add( compositeJsBundlerPlugin.getContentPathParser().createRequest(bundleRequestForm, minifierSetting) );
		}
		return possibleRequests;
	}
	
	@Override
	public List<String> getDependentContentPluginRequestPrefixes()
	{
		return Arrays.asList( "js" );
	}
	
	
	private void writeTagContent(Map<String, String> tagAttributes, RequestMode requestMode, BundleSet bundleSet, Locale locale, Writer writer, String version) throws IOException {
		try
		{
			List<String> possibleRequests = getGeneratedRequests(requestMode, tagAttributes, bundleSet, locale, version);
			for (String request : possibleRequests) {
				writeScriptTag(requestMode, bundleSet.getBundlableNode().app(), writer, request, version);
			}
		}
		catch (MalformedTokenException | ContentProcessingException e)
		{
			throw new IOException(e);
		}
	}
	
	private void writeScriptTag(RequestMode requestMode, App app, Writer writer, String contentPath, String version) throws IOException, MalformedTokenException {
		String requestPath = (requestMode == RequestMode.Dev) ? app.createDevBundleRequest(contentPath, version) : app.createProdBundleRequest(contentPath, version);
		writer.write("<script type='text/javascript' src='" + requestPath + "'></script>\n");
	}
	
}
