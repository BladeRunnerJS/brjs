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
	
	/**
	 * BRJS calculates dependencies implicitly for additional {@link Asset}s found by plugins, for example, a JavaScript file
	 * from a blade will implicitly depend on a CSS file in the same blade once this has been located. The method will add these implicit 
	 * dependencies to the current LinkedAsset. 
	 * 
	 * @param implicitDependencies the previously retrieved implicitDependencies to be added
	 */
	void addImplicitDependencies(List<Asset> implicitDependencies);
}
