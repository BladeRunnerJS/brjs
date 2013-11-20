package org.bladerunnerjs.core.plugin.bundler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.core.plugin.VirtualProxyPlugin;
import org.bladerunnerjs.model.AssetFileAccessor;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

public class VirtualProxyBundlerPlugin extends VirtualProxyPlugin implements BundlerPlugin {
	private BundlerPlugin bundlerPlugin;
	
	public VirtualProxyBundlerPlugin(BundlerPlugin bundlerPlugin) {
		super(bundlerPlugin);
		this.bundlerPlugin = bundlerPlugin;
	}
	
	@Override
	public String getTagName() {
		return bundlerPlugin.getTagName();
	}
	
	@Override
	public String getMimeType() {
		return bundlerPlugin.getMimeType();
	}
	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		initializePlugin();
		bundlerPlugin.writeDevTagContent(tagAttributes, bundleSet, locale, writer);
	}
	
	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		initializePlugin();
		writeProdTagContent(tagAttributes, bundleSet, locale, writer);
	}
	
	@Override
	public RequestParser getRequestParser() {
		initializePlugin();
		return bundlerPlugin.getRequestParser();
	}
	
	@Override
	public void writeContent(ParsedRequest request, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException {
		initializePlugin();
		bundlerPlugin.writeContent(request, bundleSet, os);
	}
	
	@Override
	public AssetFileAccessor getAssetFileAccessor() {
		initializePlugin();
		return bundlerPlugin.getAssetFileAccessor();
	}
	
	@Override
	public List<String> generateRequiredDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		initializePlugin();
		return bundlerPlugin.generateRequiredDevRequestPaths(bundleSet, locale);
	}
	
	@Override
	public List<String> generateRequiredProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		initializePlugin();
		return bundlerPlugin.generateRequiredProdRequestPaths(bundleSet, locale);
	}
}
