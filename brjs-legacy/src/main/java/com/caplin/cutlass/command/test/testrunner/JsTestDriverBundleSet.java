package com.caplin.cutlass.command.test.testrunner;

import java.util.List;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.plugin.AssetPlugin;

public class JsTestDriverBundleSet implements BundleSet {
	private BundleSet bundleSet;
	
	public JsTestDriverBundleSet(BundleSet bundleSet) {
		this.bundleSet = bundleSet;
	}
	
	public BundlableNode getBundlableNode() {
		return new JsTestDriverBundlableNode(bundleSet.getBundlableNode());
	}
	
	public List<String> getThemes() {
		return bundleSet.getThemes();
	}
	
	public List<SourceModule> getSourceModules() {
		return bundleSet.getSourceModules();
	}
	
	public List<AliasDefinition> getActiveAliases() {
		return bundleSet.getActiveAliases();
	}
	
	public List<AssetLocation> getResourceNodes() {
		return bundleSet.getResourceNodes();
	}
	
	public List<Asset> getResourceFiles(AssetPlugin assetProducer) {
		return bundleSet.getResourceFiles(assetProducer);
	}
}
