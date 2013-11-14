package org.bladerunnerjs.model;

import java.util.Arrays;
import java.util.List;


public class NullAssetFileAccessor implements AssetFileAccessor
{

	@Override
	public List<SourceFile> getSourceFiles(AssetContainer assetContainer)
	{
		return Arrays.asList();
	}

	@Override
	public List<LinkedAssetFile> getLinkedResourceFiles(Resources resources)
	{
		return Arrays.asList();
	}

	@Override
	public List<AssetFile> getResourceFiles(Resources resources)
	{
		return Arrays.asList();
	}

}
