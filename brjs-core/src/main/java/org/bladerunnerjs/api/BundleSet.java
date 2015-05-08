package org.bladerunnerjs.api;

import java.util.List;

/**
 * A set of assets that could be sent depending on tags and requests.
 */

public interface BundleSet {
	
	/**
	 * The method retrieves the {@link BundableNode} that represents the current BundleSet.
	 * 
	 * @return the BundableNode that represents the current BundleSet
	*/
	public BundlableNode bundlableNode();
	
	/**
	 * The method retrieves all the {@link LinkedAsset}s that are required for the current bundle.
	 * 
	 * @return a List of LinkedAssets representing all the assets that could be sent
	 */
	public List<LinkedAsset> seedAssets();
	
	/**
	 * The method retrieves all the {@link Asset}s that correspond to the specified prefixes e.g. css! or theme!.
	 * 
	 * @param prefixes optional Strings representing the prefixes that the Assets should have
	 * @return a List of Assets representing all the assets that correspond to the specified prefixes
	 */
	public List<Asset> assets(String... prefixes);
	
	/**
	 * The method retrieves all the entities of the requested {@link Asset} subtype that correspond to the specified prefixes e.g. css! or theme!.
	 * 
	 * @param assetType a Class representing an Asset subtype that will be the type of the returned Assets
	 * @param prefixes optional Strings representing the prefixes that the Assets should have
	 * @return a List of Assets subtype entities representing all the assets that correspond to the specified prefixes
	 */
	public <AT extends Asset> List<AT> assets(Class<? extends AT> assetType, String... prefixes);
	
	/**
	 * The method retrieves all the {@link Asset}s that correspond to the specified prefixes e.g. css! or theme! and Asset subtypes.
	 * 
	 * @param assetType a List of Classes representing the Asset subtypes that will be the type of the returned Assets
	 * @param prefixes optional Strings representing the prefixes that the Assets should have
	 * @return a List of Assets representing all the assets that correspond to the specified prefixes and Asset subtypes
	 */
	public List<Asset> assets(List<Class<? extends Asset>> assetTypes, String... prefixes);
	
	/**
	 * The method retrieves all the {@link SourceModule}s that may be requested for the current bundle.
	 * 
	 * @return a List of SourceModules representing all the SourceModules that may be requested for the current bundle
	 */
	public List<SourceModule> sourceModules();
	
	/**
	 * The method retrieves all the entities of the requested {@link Asset} subtype that are SourceModules for the current bundle.
	 * 
	 * @param assetType a Class representing a SourceModule subtype that will be the type of the returned SourceModules
	 * @return a List of SourceModule subtype entities representing all the SourceModules for the current bundle
	 */
	public <SMT extends SourceModule> List<SMT> sourceModules(Class<? extends SMT> assetType);
	
	/**
	 * The method retrieves all the {@link SourceModule}s that correspond to the specified SourceModule subtypes.
	 * 
	 * @param assetType a Class representing a SourceModule subtype that will be the type of the returned SourceModules
	 * @return a List of SourceModules representing all the SourceModules that correspond to the specified SourceModule subtypes
	 */
	public List<SourceModule> sourceModules(List<Class<? extends SourceModule>> assetTypes);
}
