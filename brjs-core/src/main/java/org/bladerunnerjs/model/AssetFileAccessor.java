package org.bladerunnerjs.model;

import java.util.List;


public interface AssetFileAccessor
{
	List<SourceFile> getSourceFiles(AssetLocation assetLocation);
	List<LinkedAssetFile> getLinkedResourceFiles(AssetLocation assetLocation);
	List<AssetFile> getResourceFiles(AssetLocation assetLocation);
}
