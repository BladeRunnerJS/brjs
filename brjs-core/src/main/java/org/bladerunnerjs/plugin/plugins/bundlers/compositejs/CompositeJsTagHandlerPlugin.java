package org.bladerunnerjs.plugin.plugins.bundlers.compositejs;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

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
		compositeJsBundlerPlugin = brjs.plugins().contentProvider("js");
	}
	
	@Override
	public String getTagName() {
		return "js.bundle";
	}
	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		writeTagContent(tagAttributes, true, bundleSet, locale, writer);
	}
	
	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		writeTagContent(tagAttributes, false, bundleSet, locale, writer);
	}
	
	private void writeTagContent(Map<String, String> tagAttributes, boolean isDev, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		try {
			MinifierSetting minifierSettings = new MinifierSetting(tagAttributes);
			String minifierSetting = (isDev) ? minifierSettings.devSetting() : minifierSettings.prodSetting();
			
			if(minifierSetting.equals(MinifierSetting.SEPARATE_JS_FILES)) {
				for(ContentPlugin contentPlugin : brjs.plugins().contentProviders("text/javascript")) {
					List<String> contentPaths = (isDev) ? contentPlugin.getValidDevContentPaths(bundleSet, locale) : contentPlugin.getValidProdContentPaths(bundleSet, locale);
					
					for(String contentPath : contentPaths) {
						writeScriptTag(writer, contentPath);
					}
				}
			}
			else {
				String bundleRequestForm = (isDev) ? "dev-bundle-request" : "prod-bundle-request";
				
				writeScriptTag(writer, compositeJsBundlerPlugin.getContentPathParser().createRequest(bundleRequestForm, locale, minifierSetting));
			}
		}
		catch(MalformedTokenException | ContentProcessingException e) {
			throw new IOException(e);
		}
	}
	
	private void writeScriptTag(Writer writer, String contentPath) throws IOException {
		writer.write("<script type='text/javascript' src='" + contentPath + "'></script>\n");
	}
}
