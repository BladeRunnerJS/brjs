package org.bladerunnerjs.api;

import java.util.List;

public interface BundleSet {
	public BundlableNode getBundlableNode();
	public List<LinkedAsset> seedAssets();
	public List<LinkedAsset> getLinkedAssets();
	public List<Asset> getAssets(String... prefixes);
	public <AT extends Asset> List<AT> getAssets(Class<? extends AT> assetType, String... prefixes);
	public List<Asset> getAssets(List<Class<? extends Asset>> assetTypes, String... prefixes);
	public <AT extends SourceModule> List<AT> getSourceModules(Class<? extends AT> assetType);
	public List<SourceModule> getSourceModules(List<Class<? extends SourceModule>> assetTypes);
}
