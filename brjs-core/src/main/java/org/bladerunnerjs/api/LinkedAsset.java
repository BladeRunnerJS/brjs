package org.bladerunnerjs.api;

import java.util.List;

import org.bladerunnerjs.api.model.exception.ModelOperationException;

/**
 * An extension of AssetFile, represents any AssetFile that could depend on other AssetFiles. 
 * For example an XML file that might reference a source file class.
 * 
 */
public interface LinkedAsset extends Asset {
	/**
	 * Returns a list of files this LinkedAssetFile depends on
	 * @param bundlableNode TODO
	 * @throws ModelOperationException for any exception when calculating dependencies and resolving require paths
	 * @return The list of assets
	 */
	List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException;
	void addImplicitDependencies(List<Asset> implicitDependencies);
}
