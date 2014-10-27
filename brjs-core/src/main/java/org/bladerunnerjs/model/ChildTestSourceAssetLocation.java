package org.bladerunnerjs.model;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.RootNode;


public final class ChildTestSourceAssetLocation extends AbstractChildSourceAssetLocation implements TestAssetLocation
{
	public ChildTestSourceAssetLocation(RootNode rootNode, AssetContainer assetContainer, MemoizedFile dir, AssetLocation parentAssetLocation)
	{
		super(rootNode, assetContainer, dir, parentAssetLocation, parentAssetLocation);
	}
}
