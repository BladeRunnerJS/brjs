package org.bladerunnerjs.api.plugin;

import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetContainer;


public interface AssetPlugin extends Plugin
{	
	/**
	 * The method adds the {@link Asset}s available for the specified directory, including implicit dependencies.
	 * 
	 * @param assetContainer an AssetContainer for which the AssetPlugins will be determined
	 * @param dir a MemoizedFile object specifying the AssetContainer for which the AssetPlugins will be determined
	 * @param requirePrefix a String object specifying what require prefix the newly discovered Assets should adhere to
	 * @param implicitDependencies a List of Assets specifying the already determined implicit dependencies for the container
	 * @param assetDiscoveryInitiator an AssetRegistry for the management and storage of Assets discovered
	 */
	void discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, 
			List<Asset> implicitDependencies, AssetRegistry assetDiscoveryInitiator);
}
