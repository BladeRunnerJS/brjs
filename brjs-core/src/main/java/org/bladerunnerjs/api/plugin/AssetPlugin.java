package org.bladerunnerjs.api.plugin;

import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetContainer;


public interface AssetPlugin extends Plugin
{
	/**
	 * Get the priority for this plugin. Lower priorities correspond to earlier execution.
	 * The default priority for plugins is 10.
	 * Any plugin that can detirmine with 100% confidence it can handle a given resource should define a lower priority so it is executed first and can better influence the discovery of assets.
	 * 
	 * @return the priority for this plugin. Lower priorities correspond to earlier execution.
	 */
	int priority();
	
	void discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, 
			List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator);
}
