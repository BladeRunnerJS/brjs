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
	public void writeTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, RequestMode requestMode, Locale locale, Writer writer, String version) throws IOException {
		try
		{
			List<String> possibleRequests = getGeneratedRequests(requestMode, tagAttributes, bundleSet, locale, version);
			for (String request : possibleRequests) {
				writer.write("<script type='text/javascript' src='" + request + "'></script>\n");
			}
		}
		catch (MalformedTokenException | ContentProcessingException e)
		{
			throw new IOException(e);
		}
	}
	
	@Override
	public List<String> getGeneratedContentPaths(Map<String, String> tagAttributes, BundleSet bundleSet, RequestMode requestMode, Locale locale) throws MalformedTokenException, ContentProcessingException
	{
		List<String> contentPaths = new ArrayList<String>();
		MinifierSetting minifierSettings = new MinifierSetting(tagAttributes);
		String minifierSetting = (requestMode == RequestMode.Dev) ? minifierSettings.devSetting() : minifierSettings.prodSetting();
		
		if(minifierSetting.equals(MinifierSetting.SEPARATE_JS_FILES)) {
			for(ContentPlugin contentPlugin : brjs.plugins().contentPlugins("text/javascript")) {
				contentPaths.addAll( contentPlugin.getValidContentPaths(bundleSet, requestMode) );
			}
		}
		else {
			String bundleRequestForm = (requestMode == RequestMode.Dev) ? "dev-bundle-request" : "prod-bundle-request";
			contentPaths.add( compositeJsBundlerPlugin.getContentPathParser().createRequest(bundleRequestForm, minifierSetting) );
		}
		return contentPaths;
	}
	
	@Override
	public List<String> getDependentContentPluginRequestPrefixes()
	{
		return Arrays.asList( "js" );
	}
	
	private List<String> getGeneratedRequests(RequestMode requestMode, Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, String version) throws MalformedTokenException, ContentProcessingException
	{
		List<String> requests = new ArrayList<String>();
		App app = bundleSet.getBundlableNode().app();
		for (String contentPath : getGeneratedContentPaths(tagAttributes, bundleSet, requestMode, locale)) {
			String requestPath = app.createBundleRequest(contentPath, version);
			requests.add(requestPath);
		}
		return requests;
	}
	
}
