package org.bladerunnerjs.core.plugin.taghandler.js;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.core.plugin.VirtualProxyPlugin;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.bundler.js.MinifierSetting;
import org.bladerunnerjs.core.plugin.content.ContentPlugin;
import org.bladerunnerjs.core.plugin.taghandler.AbstractTagHandlerPlugin;
import org.bladerunnerjs.core.plugin.taghandler.TagHandlerPlugin;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;

public class CompositeJsTagHandlerPlugin extends AbstractTagHandlerPlugin implements TagHandlerPlugin {
	private BRJS brjs;
	private ContentPlugin compositeJsBundlerPlugin;
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
		compositeJsBundlerPlugin = brjs.plugins().contentProvider("js");
	}
	
	@Override
	public String getTagName() {
//		return "js.bundle"; //TODO: change this back to js.bundle once the legacy tag handler is deleted
		return "new-js.bundle";
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
				for(BundlerPlugin bundlerPlugin : brjs.plugins().bundlers("text/javascript")) {
					if((bundlerPlugin.instanceOf(TagHandlerPlugin.class)) && !bundlerPlugin.equals(this)) {
						TagHandlerPlugin tagHandler = (TagHandlerPlugin) ((VirtualProxyPlugin) bundlerPlugin).getUnderlyingPlugin();
						
						if(isDev) {
							tagHandler.writeDevTagContent(tagAttributes, bundleSet, locale, writer);
						}
						else {
							tagHandler.writeProdTagContent(tagAttributes, bundleSet, locale, writer);
						}
					}
				}
			}
			else {
				String bundleRequestForm = (isDev) ? "dev-bundle-request" : "prod-bundle-request";
				
				writer.write("<script type='text/javascript' src='" + compositeJsBundlerPlugin.getContentPathParser().createRequest(bundleRequestForm, locale, minifierSetting) + "'></script>\n");
			}
		}
		catch(MalformedTokenException e) {
			throw new IOException(e);
		}
	}
}
