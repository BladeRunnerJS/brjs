package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;

/**
 * Represents the parent directory of any AssetFile, so in the example src/a/b/c/someFile.xml it would represent the src/a/b/c directory.
 * Due to legacy resources the AssetLocation for any file in resources is always the resources directory. So in the example resources/a/b/c/someFile.xml, resouces is the AssetLocation.
 *
 */
public interface AssetLocation extends BRJSNode {
	String getJsStyle();
	String requirePrefix();
	AliasDefinitionsFile aliasDefinitionsFile();
	List<LinkedAsset> seedResources();
	List<LinkedAsset> seedResources(String fileExtension);
	List<Asset> bundleResources(String fileExtension);
	AssetContainer getAssetContainer();
	List<AssetLocation> getDependentAssetLocations();
}
