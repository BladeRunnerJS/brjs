package org.bladerunnerjs.api.plugin;

import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetContainer;
/**
 * An interface for the management and recording of existing {@link Asset}s.
 */

public interface AssetRegistry
{
	/**
	 * The method adds the specified {@link LinkedAsset} to the existing list of seed assets.
	 * 
	 * @param asset a LinkedAsset object that will be added to the existing list of seed assets
	 */
	public void registerSeedAsset(LinkedAsset asset);
	
	/**
	 * The method adds an already registered {@link LinkedAsset} to the list of seed assets.
	 * 
	 * @param asset an already registered LinkedAsset object that will be added to the list of seed assets
	 */
	public void promoteRegisteredAssetToSeed(LinkedAsset asset);
	
	/**
	 * The method registers the specified {@link LinkedAsset} by adding it to the assets for the current directory, provided it has not been
	 * registered beforehand.
	 * 
	 * @param asset a LinkedAsset object that will be registered by adding it to the assets for the current directory
	 */
	public void registerAsset(Asset asset);
	
	/**
	 * The method verifies whether the specified require path is the valid require path for one of its seed assets.
	 * 
	 * @param requirePath a String object that will be verified to establish whether it denotes an Asset part of the seed assets for the registry.
	 * @return boolean returns a value of 'true' if the requirePath denotes an Asset part of the seed assets for the registry and 'false' otherwise.
	 */
	boolean hasSeedAsset(String requirePath);
	
	/**
	 * The method verifies whether the specified require path is the valid require path for one of its registered assets.
	 * 
	 * @param requirePath a String object that will be verified to establish whether it denotes an Asset part of the registered assets
	 * for the registry.
	 * @return boolean returns a value of 'true' if the requirePath denotes an Asset part of the registered assets for the registry and 
	 * 'false' otherwise.
	 */
	boolean hasRegisteredAsset(String requirePath);
	
	/**
	 * The method retrieves the registered {@link Asset} identified by the specified require path within the current registry.
	 * 
	 * @param requirePath a String object that will determine which Asset will be retrieved
	 * @return the Asset object identified by the specified require path within the current registry.
	 */
	public Asset getRegisteredAsset(String requirePath);
	
	/**
	 * The method retrieves the List of {@link Asset}s identified by the AssetPlugins available for the current {@link AssetContainer}, 
	 * specified by a {@link MemoizedFile}.
	 * 
	 * @param dir a MemoizedFile object specifying the assetContainer for which the AssetPlugins will be determined
	 * @param requirePrefix a String object specifying what require prefix the newly discovered Assets should adhere to
	 * @param implicitDependencies a List of Assets specifying the already determined implicit dependencies for the container
	 * @return the list of Assets identified by the AssetPlugins available for the current AssetContainer}
	 */
	public List<Asset> discoverFurtherAssets(MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies);
}
