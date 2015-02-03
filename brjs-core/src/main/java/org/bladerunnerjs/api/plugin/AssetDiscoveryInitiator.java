package org.bladerunnerjs.api.plugin;

import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.memoization.MemoizedFile;


public interface AssetDiscoveryInitiator
{
	public void registerAsset(Asset asset);
	public void discoverFurtherAssets(MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies);
}
