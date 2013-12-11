package org.bladerunnerjs.model;

import java.util.List;

/**
 * Represents a location that can contain assets (src or resources) such as an Aspect, Blade or Workbench.
 *
 */
public interface AssetContainer extends BRJSNode {
	App getApp();
	String namespace();
	String requirePrefix();
	List<SourceModule> sourceModules();
	SourceModule sourceModule(String requirePath);
	AssetLocation assetLocation(String locationPath);
	List<AssetLocation> assetLocations();
}
