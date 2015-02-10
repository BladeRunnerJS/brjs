package org.bladerunnerjs.api;

import java.util.List;

import org.bladerunnerjs.api.aliasing.AliasDefinition;
import org.bladerunnerjs.api.plugin.LegacyAssetPlugin;
import org.bladerunnerjs.model.BundlableNode;

public interface BundleSet {
	public BundlableNode getBundlableNode();
	public List<String> getThemes();
	public List<Asset> getAssets();
	public List<SourceModule> getSourceModules();
	public List<AliasDefinition> getActiveAliases();
	public List<AssetLocation> getResourceNodes();
	public List<Asset> getResourceFiles(LegacyAssetPlugin assetProducer);
}
