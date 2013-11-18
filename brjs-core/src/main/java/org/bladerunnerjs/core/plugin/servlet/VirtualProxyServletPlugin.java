package org.bladerunnerjs.core.plugin.servlet;

import java.io.OutputStream;

import org.bladerunnerjs.core.plugin.VirtualProxyPlugin;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

public class VirtualProxyServletPlugin extends VirtualProxyPlugin implements ServletPlugin {
	private ServletPlugin servletPlugin;
	
	public VirtualProxyServletPlugin(ServletPlugin servletPlugin) {
		super(servletPlugin);
		this.servletPlugin = servletPlugin;
	}
	
	@Override
	public String getMimeType() {
		return servletPlugin.getMimeType();
	}
	
	@Override
	public RequestParser getRequestParser() {
		initializePlugin();
		return servletPlugin.getRequestParser();
	}
	
	@Override
	public void handleRequest(ParsedRequest request, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException {
		initializePlugin();
		servletPlugin.handleRequest(request, bundleSet, os);
	}
}
