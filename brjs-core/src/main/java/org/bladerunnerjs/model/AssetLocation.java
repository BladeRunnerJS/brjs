package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.model.exception.RequirePathException;

/**
 * Represents the parent directory of any AssetFile, so in the example src/a/b/c/someFile.xml it would represent the src/a/b/c directory.
 * Due to legacy resources the AssetLocation for any file in resources is always the resources directory. So in the example resources/a/b/c/someFile.xml, resouces is the AssetLocation.
 *
 */
public interface AssetLocation extends BRJSNode {
	String getJsStyle();
	String requirePrefix() throws RequirePathException;
	String getNamespace() throws RequirePathException;
	List<SourceModule> getSourceModules();
	SourceModule getSourceModuleWithRequirePath(String requirePath) throws RequirePathException;
	AliasDefinitionsFile aliasDefinitionsFile();
	List<LinkedAsset> seedResources();
	List<LinkedAsset> seedResources(String fileExtension);
	List<Asset> bundleResources(String fileExtension);
	AssetContainer getAssetContainer();
	List<AssetLocation> getDependentAssetLocations();
	<A extends Asset> A obtainAsset(File assetFileOrDir, Class<? extends A> assetClass) throws AssetFileInstantationException;
	<A extends Asset> List<A> obtainMatchingAssets(IOFileFilter fileFilter, Class<A> assetListClass, Class<? extends A> assetClass) throws AssetFileInstantationException;
}
