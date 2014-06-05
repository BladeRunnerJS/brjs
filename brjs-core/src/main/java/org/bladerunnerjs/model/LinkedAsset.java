package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.model.exception.ModelOperationException;

/**
 * An extension of AssetFile, represents any AssetFile that could depend on other AssetFiles. 
 * For example an XML file that might reference a source file class.
 * 
 */
public interface LinkedAsset extends Asset {
	/**
	 * Returns a list of files this LinkedAssetFile depends on
	 * @param bundlableNode TODO
	 */
	List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException;
	List<String> getAliasNames() throws ModelOperationException;
	List<String> getProvidedRequirePaths();
}
