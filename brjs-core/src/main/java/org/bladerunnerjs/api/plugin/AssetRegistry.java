package org.bladerunnerjs.api.plugin;

import java.util.List;
import java.util.Set;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;


public interface AssetRegistry
{
	public void registerSeedAsset(LinkedAsset asset);
	public void promoteRegisteredAssetToSeed(LinkedAsset asset);
	public void registerAsset(Asset asset);
	boolean hasSeedAsset(String requirePath);
	boolean hasRegisteredAsset(String requirePath);
	public Asset getRegisteredAsset(String requirePath);
	public Set<Asset> getRegisteredAssets();
	public Set<LinkedAsset> getRegisteredSeedAssets();
	public List<Asset> discoverFurtherAssets(MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies);
}
