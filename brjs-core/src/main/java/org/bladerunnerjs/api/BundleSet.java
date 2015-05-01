package org.bladerunnerjs.api;

import java.util.List;

public interface BundleSet {
	public BundlableNode bundlableNode();
	public List<LinkedAsset> seedAssets();
	
	public List<Asset> assets(String... prefixes);
	public <AT extends Asset> List<AT> assets(Class<? extends AT> assetType, String... prefixes);
	public List<Asset> assets(List<Class<? extends Asset>> assetTypes, String... prefixes);
	
	public List<SourceModule> sourceModules();
	public <SMT extends SourceModule> List<SMT> sourceModules(Class<? extends SMT> assetType);
	public List<SourceModule> sourceModules(List<Class<? extends SourceModule>> assetTypes);
}
