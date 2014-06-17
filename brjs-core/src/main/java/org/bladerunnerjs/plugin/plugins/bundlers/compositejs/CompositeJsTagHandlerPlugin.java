package org.bladerunnerjs.plugin.plugins.bundlers.compositejs;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.ContentPlugin;
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
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer, String version) throws IOException {
		writeTagContent(tagAttributes, true, bundleSet, locale, writer, version);
	}
	
	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer, String version) throws IOException {
		writeTagContent(tagAttributes, false, bundleSet, locale, writer, version);
	}
	
	private void writeTagContent(Map<String, String> tagAttributes, boolean isDev, BundleSet bundleSet, String locale, Writer writer, String version) throws IOException {
		try {
			MinifierSetting minifierSettings = new MinifierSetting(tagAttributes);
			String minifierSetting = (isDev) ? minifierSettings.devSetting() : minifierSettings.prodSetting();
			
			if(minifierSetting.equals(MinifierSetting.SEPARATE_JS_FILES)) {
				for(ContentPlugin contentPlugin : brjs.plugins().contentPlugins("text/javascript")) {
					List<String> contentPaths = (isDev) ? contentPlugin.getValidDevContentPaths(bundleSet, (String[]) null) : contentPlugin.getValidProdContentPaths(bundleSet, (String[]) null);
					
					for(String contentPath : contentPaths) {
						writeScriptTag(isDev, bundleSet.getBundlableNode().app(), writer, contentPath, version);
					}
				}
			}
			else {
				String bundleRequestForm = (isDev) ? "dev-bundle-request" : "prod-bundle-request";
				
				writeScriptTag(isDev, bundleSet.getBundlableNode().app(), writer,
					compositeJsBundlerPlugin.getContentPathParser().createRequest(bundleRequestForm, minifierSetting), version);
			}
		}
		catch(MalformedTokenException | ContentProcessingException e) {
			throw new IOException(e);
		}
	}
	
	private void writeScriptTag(boolean isDev, App app, Writer writer, String contentPath, String version) throws IOException, MalformedTokenException {
		String requestPath = (isDev) ? app.createDevBundleRequest(contentPath, version) : app.createProdBundleRequest(contentPath, version);
		writer.write("<script type='text/javascript' src='" + requestPath + "'></script>\n");
	}
}
