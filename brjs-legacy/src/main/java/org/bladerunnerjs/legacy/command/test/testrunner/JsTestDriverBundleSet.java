package org.bladerunnerjs.legacy.command.test.testrunner;

import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.aliasing.AliasDefinition;
import org.bladerunnerjs.api.plugin.AssetPlugin;
import org.bladerunnerjs.model.BundlableNode;

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
	
	public List<Asset> getResourceFiles() {
		return bundleSet.getResourceFiles();
	}
	
	public List<Asset> getResourceFiles(AssetPlugin assetProducer) {
		return bundleSet.getResourceFiles(assetProducer);
	}
}
