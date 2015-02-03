package org.bladerunnerjs.model;

import java.util.List;
import java.util.Set;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.api.model.exception.RequirePathException;

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
	
	String canonicaliseRequirePath(String requirePath) throws RequirePathException;
	
	/**
	 * Returns all AssetContainers whose assets can be referred to by assets in this AssetContainer
	 */
	List<AssetContainer> scopeAssetContainers();
	
	//TODO: remove this, its only temporary to allow plugin API refactoring and removing aliases from the model
	AliasDefinitionsFile aliasDefinitionsFile(String path);
}
