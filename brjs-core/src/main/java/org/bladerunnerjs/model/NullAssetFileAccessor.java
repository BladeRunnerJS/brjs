package org.bladerunnerjs.model;

import java.util.Arrays;
import java.util.List;


public class NullAssetFileAccessor implements AssetFileAccessor
{

	@Override
	public List<SourceFile> getSourceFiles(AssetLocation assetLocation)
	{
		return Arrays.asList();
	}

	@Override
	public List<LinkedAssetFile> getLinkedResourceFiles(AssetLocation assetLocation)
	{
		return Arrays.asList();
	}

	@Override
	public List<AssetFile> getResourceFiles(AssetLocation assetLocation)
	{
		return Arrays.asList();
	}

}
