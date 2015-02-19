package org.bladerunnerjs.model;

import java.util.List;
import java.util.Set;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.LinkedAsset;

/**
 * Represents a location that can contain assets (src or resources) such as an Aspect, Blade or Workbench.
 *
 */
public interface AssetContainer extends BRJSNode {
	App app();
	String requirePrefix();
	boolean isNamespaceEnforced();
	Set<LinkedAsset> linkedAssets();
	LinkedAsset linkedAsset(String requirePath);
	AssetLocation assetLocation(String locationPath);
	List<AssetLocation> assetLocations();
	RootAssetLocation rootAssetLocation();
	List<String> getAssetLocationPaths();
	/**
	 * Returns all AssetContainers whose assets can be referred to by assets in this AssetContainer
	 */
	List<AssetContainer> scopeAssetContainers();
}
