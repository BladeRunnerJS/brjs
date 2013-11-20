package org.bladerunnerjs.core.plugin.servlet;

import java.io.OutputStream;
import java.util.List;

import org.bladerunnerjs.core.plugin.VirtualProxyPlugin;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

public class VirtualProxyContentPlugin extends VirtualProxyPlugin implements ContentPlugin {
	private ContentPlugin contentPlugin;
	
	public VirtualProxyContentPlugin(ContentPlugin contentPlugin) {
		super(contentPlugin);
		this.contentPlugin = contentPlugin;
	}
	
	@Override
	public String getMimeType() {
		return contentPlugin.getMimeType();
	}
	
	@Override
	public RequestParser getRequestParser() {
		initializePlugin();
		return contentPlugin.getRequestParser();
	}
	
	@Override
	public void writeContent(ParsedRequest request, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException {
		initializePlugin();
		contentPlugin.writeContent(request, bundleSet, os);
	}

	@Override
	public List<String> generateRequiredDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException
	{
		initializePlugin();
		return contentPlugin.generateRequiredDevRequestPaths(bundleSet, locale);
	}

	@Override
	public List<String> generateRequiredProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException
	{
		initializePlugin();
		return contentPlugin.generateRequiredProdRequestPaths(bundleSet, locale);
	}
}
