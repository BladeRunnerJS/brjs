package org.bladerunnerjs.api.plugin;

import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetContainer;


public interface AssetPlugin extends Plugin
{	
	void discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, 
			List<Asset> implicitDependencies, AssetRegistry assetDiscoveryInitiator);
}
