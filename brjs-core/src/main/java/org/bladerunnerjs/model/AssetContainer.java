package org.bladerunnerjs.model;

import java.util.List;
import java.util.Set;

/**
 * Represents a location that can contain assets (src or resources) such as an Aspect, Blade or Workbench.
 *
 */
public interface AssetContainer extends BRJSNode {
	App app();
	String requirePrefix();
	String namespace();
	boolean isNamespaceEnforced();
	Set<SourceModule> sourceModules();
	SourceModule sourceModule(String requirePath);
	List<AssetContainer> scopeAssetContainers();
	AssetLocation assetLocation(String locationPath);
	List<AssetLocation> assetLocations();
	List<String> getAssetLocationPaths();
}
