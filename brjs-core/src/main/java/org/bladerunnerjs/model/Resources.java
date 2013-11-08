package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.model.file.AliasDefinitionsFile;


public interface Resources {
	File dir();
	AliasDefinitionsFile aliasDefinitions();
	List<LinkedAssetFile> seedResources();
	List<LinkedAssetFile> seedResources(String fileExtension);
	List<AssetFile> bundleResources(String fileExtension);
}
