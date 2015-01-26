package org.bladerunnerjs.model;

import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.RootNode;


public final class TestSourceAssetLocation extends AbstractSourceAssetLocation implements TestAssetLocation
{
	public TestSourceAssetLocation(RootNode rootNode, AssetContainer assetContainer, MemoizedFile dir, AssetLocation parentAssetLocation, AssetLocation... dependentAssetLocations)
	{
		super(rootNode, assetContainer, dir, parentAssetLocation, dependentAssetLocations);
	}
	
	protected AssetLocation createNewAssetLocationForChildDir(MemoizedFile dir, AssetLocation parentAssetLocation)
	{
		return new ChildTestSourceAssetLocation(assetContainer().root(), assetContainer(), dir, parentAssetLocation);
	}
}
