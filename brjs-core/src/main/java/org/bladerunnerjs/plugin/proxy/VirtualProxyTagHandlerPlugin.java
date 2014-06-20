package org.bladerunnerjs.plugin.proxy;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.TagHandlerPlugin;

public class VirtualProxyTagHandlerPlugin extends VirtualProxyPlugin implements TagHandlerPlugin {
	private TagHandlerPlugin tagHandlerPlugin;
	
	public VirtualProxyTagHandlerPlugin(TagHandlerPlugin tagHandlerPlugin) {
		super(tagHandlerPlugin);
		this.tagHandlerPlugin = tagHandlerPlugin;
	}
	
	@Override
	public String getTagName() {
		return tagHandlerPlugin.getTagName();
	}
	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, Writer writer, String version) throws IOException {
		initializePlugin();
		tagHandlerPlugin.writeDevTagContent(tagAttributes, bundleSet, locale, writer, version);
	}
	
	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, Writer writer, String version) throws IOException {
		initializePlugin();
		tagHandlerPlugin.writeProdTagContent(tagAttributes, bundleSet, locale, writer, version);
	}
	
}
