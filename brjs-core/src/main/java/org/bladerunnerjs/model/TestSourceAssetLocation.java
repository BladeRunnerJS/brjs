package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.engine.RootNode;


public final class TestSourceAssetLocation extends AbstractSourceAssetLocation implements TestAssetLocation
{
	public TestSourceAssetLocation(RootNode rootNode, AssetContainer assetContainer, File dir, AssetLocation parentAssetLocation, AssetLocation... dependentAssetLocations)
	{
		super(rootNode, assetContainer, dir, parentAssetLocation, dependentAssetLocations);
	}
	
	protected AssetLocation createNewAssetLocationForChildDir(File dir, AssetLocation parentAssetLocation)
	{
		return new ChildTestSourceAssetLocation(assetContainer().root(), assetContainer(), dir, parentAssetLocation);
	}
}
