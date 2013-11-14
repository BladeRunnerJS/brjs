package org.bladerunnerjs.model;

import java.util.List;


public interface AssetFileAccessor
{
	List<SourceFile> getSourceFiles(AssetContainer assetContainer);
	List<LinkedAssetFile> getLinkedResourceFiles(Resources resources);
	List<AssetFile> getResourceFiles(Resources resources);
}
