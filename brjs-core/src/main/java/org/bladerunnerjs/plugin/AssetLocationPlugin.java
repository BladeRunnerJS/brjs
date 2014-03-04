package org.bladerunnerjs.plugin;

import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;

/**
 * Asset location plug-ins allow new asset directory structures to be supported within the model.
 * 
 * <p>Directory structures like BRJS conformant libraries, BRJS third-party libraries, Node.js libraries and Bower libraries all need
 * special handling to determine what assets and source modules exist, and how assets should be bundled when source modules are used.
 * Asset-location plug-ins can indirectly control what source modules and assets will be found by controlling the asset-locations that
 * are found, and can control how assets are bundled by controlling the relationship between source modules and asset-locations.</p>
 */
public interface AssetLocationPlugin extends Plugin {
	/**
	 * Returns <code>true</code> if this plug-in is able to generate asset locations for the given asset-container.
	 * 
	 * @param assetContainer The asset-container that asset-locations will need to be generated for.
	 */
	boolean canHandleAssetContainer(AssetContainer assetContainer);
	
	/**
	 * Return a list of asset locations for the given asset-container.
	 * 
	 * @param assetContainer The asset-container to provide asset-locations for.
	 * @param assetLocationCache A map of potentially re-usable asset-locations previously created by this plug-in.
	 */
	List<AssetLocation> getAssetLocations(AssetContainer assetContainer, Map<String, AssetLocation> assetLocationCache);
}
