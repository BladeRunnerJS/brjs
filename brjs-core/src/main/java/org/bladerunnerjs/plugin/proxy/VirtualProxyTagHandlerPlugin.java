package org.bladerunnerjs.plugin.proxy;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.TagHandlerPlugin;
import org.bladerunnerjs.model.RequestMode;

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
	public void writeTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, RequestMode requestMode, Locale locale, Writer writer, String version) throws IOException {
		initializePlugin();
		tagHandlerPlugin.writeTagContent(tagAttributes, bundleSet, requestMode, locale, writer, version);
	}

	@Override
	public List<String> getDependentContentPluginRequestPrefixes()
	{
		initializePlugin();
		return tagHandlerPlugin.getDependentContentPluginRequestPrefixes();
	}

	@Override
	public List<String> getGeneratedContentPaths(Map<String, String> tagAttributes, BundleSet bundleSet, RequestMode requestMode, Locale locale) throws MalformedTokenException, ContentProcessingException
	{
		initializePlugin();
		return tagHandlerPlugin.getGeneratedContentPaths(tagAttributes, bundleSet, requestMode, locale);
	}
	
}
