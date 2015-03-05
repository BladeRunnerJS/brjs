package org.bladerunnerjs.api.plugin;

import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;


public interface AssetDiscoveryInitiator
{
	public void registerSeedAsset(LinkedAsset asset);
	public void promoteRegisteredAssetToSeed(LinkedAsset asset);
	public void registerAsset(Asset asset);
	boolean hasSeedAsset(String requirePath);
	boolean hasRegisteredAsset(String requirePath);
	public Asset getRegisteredAsset(String requirePath);
	public List<Asset> discoverFurtherAssets(MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies);
}
