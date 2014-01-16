package org.bladerunnerjs.plugin;

import java.util.List;

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
	 * Return a list of asset locations for the given asset-container.
	 * 
	 * @param assetContainer The asset-container to provide asset-locations for.
	 * @return an array of asset-locations if the plug-in recognizes the given asset-container, or <code>null</code> otherwise.
	 */
	List<AssetLocation> getAssetLocations(AssetContainer assetContainer);
}
