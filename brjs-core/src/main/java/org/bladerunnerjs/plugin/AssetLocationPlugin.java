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
public interface AssetLocationPlugin extends OrderedPlugin {
	List<String> getAssetLocationDirectories(AssetContainer assetContainer);
	List<String> getSeedAssetLocationDirectories(AssetContainer assetContainer);
	AssetLocation createAssetLocation(AssetContainer assetContainer, String dirPath, Map<String, AssetLocation> assetLocationsMap);
	boolean allowFurtherProcessing();
}
