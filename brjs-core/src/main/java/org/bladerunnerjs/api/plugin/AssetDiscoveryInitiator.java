package org.bladerunnerjs.api.plugin;

import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.memoization.MemoizedFile;


public interface AssetDiscoveryInitiator
{
	public void registerAsset(Asset asset);
	public Asset getRegisteredAsset(String requirePath);
	public void discoverFurtherAssets(MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies);
	boolean hasRegisteredAsset(String requirePath);
}
