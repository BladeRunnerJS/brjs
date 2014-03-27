package org.bladerunnerjs.plugin.proxy;

import java.io.OutputStream;
import java.util.List;

import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;

public class VirtualProxyContentPlugin extends VirtualProxyPlugin implements ContentPlugin {
	private ContentPlugin contentPlugin;
	
	public VirtualProxyContentPlugin(ContentPlugin contentPlugin) {
		super(contentPlugin);
		this.contentPlugin = contentPlugin;
	}
	
	@Override
	public String getRequestPrefix() {
		return contentPlugin.getRequestPrefix();
	}
	
	@Override
	public String getGroupName() {
		return contentPlugin.getGroupName();
	}
	
	@Override
	public List<String> getPluginsThatMustAppearBeforeThisPlugin() {
		return contentPlugin.getPluginsThatMustAppearBeforeThisPlugin();
	}
	
	@Override
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return contentPlugin.getPluginsThatMustAppearAfterThisPlugin();
	}
	
	@Override
	public ContentPathParser getContentPathParser() {
		initializePlugin();
		return contentPlugin.getContentPathParser();
	}
	
	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws ContentProcessingException {
		initializePlugin();
		contentPlugin.writeContent(contentPath, bundleSet, os);
	}

	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException
	{
		initializePlugin();
		return contentPlugin.getValidDevContentPaths(bundleSet, locales);
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException
	{
		initializePlugin();
		return contentPlugin.getValidProdContentPaths(bundleSet, locales);
	}
	
}
