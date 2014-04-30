package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.plugin.AssetPlugin;

/**
 * Represents the parent directory of any AssetFile, so in the example src/a/b/c/someFile.xml it would represent the src/a/b/c directory.
 * Due to legacy resources the AssetLocation for any file in resources is always the resources directory. So in the example resources/a/b/c/someFile.xml, resouces is the AssetLocation.
 *
 */
public interface AssetLocation extends BRJSNode {
	String jsStyle();
	String requirePrefix();
	void assertIdentifierCorrectlyNamespaced(String identifier) throws NamespaceException, RequirePathException;
	SourceModule sourceModule(String requirePath) throws RequirePathException;
	AliasDefinitionsFile aliasDefinitionsFile();
	List<LinkedAsset> seedResources();
	List<Asset> bundleResources(AssetPlugin assetProducer);
	AssetContainer assetContainer();
	List<AssetLocation> dependentAssetLocations();
	<A extends Asset> A obtainAsset(Class<? extends A> assetClass, File dir, String assetName) throws AssetFileInstantationException;
	
	/**
	 * Returns a list of assets matching 'assetFilter'. 
	 * 
	 * @param assetFilter The filter applied to each asset
	 * @param assetClass The interface of the asset to return.
	 * @param instantiateAssetClass The class to instantiate for each matched asset.
	 * @return The list of assets.
	 * @throws AssetFileInstantationException
	 */
	<A extends Asset> List<A> obtainMatchingAssets(AssetFilter assetFilter, Class<A> assetClass, Class<? extends A> instantiateAssetClass) throws AssetFileInstantationException;
}
