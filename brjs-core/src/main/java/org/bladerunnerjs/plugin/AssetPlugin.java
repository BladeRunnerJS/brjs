package org.bladerunnerjs.plugin;

import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;

/**
 * Asset plug-ins allow new implementations of {@link SourceModule}, {@link LinkedAsset} &amp; {@link Asset} to be supported within the model.
 * 
 * <p>It is the responsibility of each asset plug-in to provide an identical list of objects each time {@link #getSourceModules getSourceModules()},
 * {@link #getLinkedAssets getLinkedAssets()} or {@link #getAssets getAssets()} are invoked for the same {@link AssetLocation}, assuming the disk
 * contents are unchanged of course. The {@link org.bladerunnerjs.model.AssetLocation#obtainAsset AssetLocation.obtainAsset()} &amp;
 * {@link org.bladerunnerjs.model.AssetLocation#obtainMatchingAssets AssetLocation.obtainMatchingAssets()} methods provide help with this, while also being designed to
 * be performant.</p>
 */
public interface AssetPlugin extends Plugin {
	/**
	 * Return a list of all source modules discovered within the given {@link AssetLocation}.
	 * 
	 * @param assetLocation The asset location to search within.
	 * @return A list of all source modules discovered at this location, or an empty list if none were discovered.
	 */
	List<SourceModule> getSourceModules(AssetLocation assetLocation);
	
	/**
	 * Return a list of all *test* source modules discovered within the given {@link AssetLocation}. 
	 * The assetLocation will be a location that exists inside of a {@link TestPack}. 
	 * 
	 * @param assetLocation The asset location to search within.
	 * @return A list of all test source modules discovered at this location, or an empty list if none were discovered.
	 */
	List<SourceModule> getTestSourceModules(AssetLocation assetLocation);
	
	/**
	 * Return a list of all linked assets discovered within the given {@link AssetLocation}.
	 * 
	 * @param assetLocation The asset location to search within.
	 * @return A list of all linked assets discovered at this location, or an empty list if none were discovered.
	 */
	List<LinkedAsset> getLinkedAssets(AssetLocation assetLocation);
	
	/**
	 * Return a list of all assets discovered within the given {@link AssetLocation}.
	 * 
	 * @param assetLocation The asset location to search within.
	 * @return A list of all assets discovered at this location, or an empty list if none were discovered.
	 */
	List<Asset> getAssets(AssetLocation assetLocation);
}
