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
		writeTagContent(tagAttributes, true, bundleSet, locale, writer, version);
	}
	
	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, Writer writer, String version) throws IOException {
		writeTagContent(tagAttributes, false, bundleSet, locale, writer, version);
	}	
	
	@Override
	public List<String> getGeneratedDevContentPaths(Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale) throws MalformedTokenException, ContentProcessingException
	{
		return getGeneratedContentPaths(true, tagAttributes, bundleSet, locale);
	}
	
	@Override
	public List<String> getGeneratedProdContentPaths(Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale) throws MalformedTokenException, ContentProcessingException
	{
		return getGeneratedContentPaths(false, tagAttributes, bundleSet, locale);
	}
	
	@Override
	public List<String> getDependentContentPluginRequestPrefixes()
	{
		return Arrays.asList( "js" );
	}
	
	
	
	
	
	private List<String> getGeneratedContentPaths(boolean isDev, Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale) throws MalformedTokenException, ContentProcessingException
	{
		List<String> contentPaths = new ArrayList<String>();
		MinifierSetting minifierSettings = new MinifierSetting(tagAttributes);
		String minifierSetting = (isDev) ? minifierSettings.devSetting() : minifierSettings.prodSetting();
		
		if(minifierSetting.equals(MinifierSetting.SEPARATE_JS_FILES)) {
			for(ContentPlugin contentPlugin : brjs.plugins().contentPlugins("text/javascript")) {
				contentPaths.addAll( (isDev) ? contentPlugin.getValidDevContentPaths(bundleSet) : contentPlugin.getValidProdContentPaths(bundleSet) );
			}
		}
		else {
			String bundleRequestForm = (isDev) ? "dev-bundle-request" : "prod-bundle-request";
			contentPaths.add( compositeJsBundlerPlugin.getContentPathParser().createRequest(bundleRequestForm, minifierSetting) );
		}
		return contentPaths;
	}
	
	private List<String> getGeneratedRequests(boolean isDev, Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, String version) throws MalformedTokenException, ContentProcessingException
	{
		List<String> requests = new ArrayList<String>();
		App app = bundleSet.getBundlableNode().app();
		for (String contentPath : getGeneratedContentPaths(isDev, tagAttributes, bundleSet, locale)) {
			String requestPath = (isDev) ? app.createDevBundleRequest(contentPath, version) : app.createProdBundleRequest(contentPath, version);
			requests.add(requestPath);
		}
		return requests;
	}
	
	private void writeTagContent(Map<String, String> tagAttributes, boolean isDev, BundleSet bundleSet, Locale locale, Writer writer, String version) throws IOException {
		try
		{
			List<String> possibleRequests = getGeneratedRequests(isDev, tagAttributes, bundleSet, locale, version);
			for (String request : possibleRequests) {
				writer.write("<script type='text/javascript' src='" + request + "'></script>\n");
			}
		}
		catch (MalformedTokenException | ContentProcessingException e)
		{
			throw new IOException(e);
		}
	}
	
}
