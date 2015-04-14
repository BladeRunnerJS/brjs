package org.bladerunnerjs.api;

import java.util.List;

public interface BundleSet {
	public BundlableNode getBundlableNode();
	public List<LinkedAsset> seedAssets();
	public List<LinkedAsset> getLinkedAssets();
	public List<Asset> getAssets(String... prefixes);
	public List<Asset> getAssets(List<String> prefixes, List<Class<? extends Asset>> assetTypes);
	public List<SourceModule> getSourceModules(String... prefixes);
}
