package org.bladerunnerjs.model;

import java.util.LinkedList;
import java.util.List;


public class NullAssetFileAccessor implements AssetFileAccessor
{

	@Override
	public List<SourceFile> getSourceFiles(SourceLocation sourceLocation)
	{
		return new LinkedList<SourceFile>();
	}

	@Override
	public List<LinkedAssetFile> getLinkedResourceFiles(Resources resources)
	{
		return new LinkedList<LinkedAssetFile>();
	}

	@Override
	public List<AssetFile> getResourceFiles(Resources resources)
	{
		return new LinkedList<AssetFile>();
	}

}
