package org.bladerunnerjs.model;

import java.util.List;
import java.util.Map;

/**
 * Represents a location that can contain assets (src or resources) such as an Aspect, Blade or Workbench.
 *
 */
public interface AssetContainer extends BRJSNode {
	App app();
	String requirePrefix();
	String namespace();
	boolean isNamespaceEnforced();
	List<SourceModule> sourceModules();
	SourceModule sourceModule(String requirePath);
	AssetLocation assetLocation(String locationPath);
	List<AssetLocation> assetLocations();
	Map<String,AssetLocation> namedAssetLocations();
}
