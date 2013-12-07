package org.bladerunnerjs.plugin.bundler;

import java.io.OutputStream;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.plugin.VirtualProxyPlugin;
import org.bladerunnerjs.utility.ContentPathParser;

public class VirtualProxyBundlerPlugin extends VirtualProxyPlugin implements BundlerPlugin {
	private BundlerPlugin bundlerPlugin;
	
	public VirtualProxyBundlerPlugin(BundlerPlugin bundlerPlugin) {
		super(bundlerPlugin);
		this.bundlerPlugin = bundlerPlugin;
	}
	
	@Override
	public String getRequestPrefix() {
		return bundlerPlugin.getRequestPrefix();
	}
	
	@Override
	public String getMimeType() {
		return bundlerPlugin.getMimeType();
	}
	
	@Override
	public ContentPathParser getContentPathParser() {
		initializePlugin();
		return bundlerPlugin.getContentPathParser();
	}
	
	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException {
		initializePlugin();
		bundlerPlugin.writeContent(contentPath, bundleSet, os);
	}
	
	@Override
	public List<String> getValidDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		initializePlugin();
		return bundlerPlugin.getValidDevRequestPaths(bundleSet, locale);
	}
	
	@Override
	public List<String> getValidProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		initializePlugin();
		return bundlerPlugin.getValidProdRequestPaths(bundleSet, locale);
	}

	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation) {
		initializePlugin();
		return bundlerPlugin.getSourceModules(assetLocation);
	}

	@Override
	public List<LinkedAsset> getLinkedResourceFiles(AssetLocation assetLocation) {
		initializePlugin();
		return bundlerPlugin.getLinkedResourceFiles(assetLocation);
	}

	@Override
	public List<Asset> getResourceFiles(AssetLocation assetLocation) {
		initializePlugin();
		return bundlerPlugin.getResourceFiles(assetLocation);
	}
}
