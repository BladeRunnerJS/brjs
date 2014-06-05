package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.plugin.AssetPlugin;

public interface BundleSet {
	public BundlableNode getBundlableNode();
	public List<SourceModule> getSourceModules();
	public List<AliasDefinition> getActiveAliases();
	public List<AssetLocation> getResourceNodes();
	public List<Asset> getResourceFiles(AssetPlugin assetProducer);
}
