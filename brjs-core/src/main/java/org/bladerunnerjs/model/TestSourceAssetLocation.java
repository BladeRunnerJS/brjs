package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.engine.RootNode;


public final class TestSourceAssetLocation extends AbstractSourceAssetLocation implements TestAssetLocation
{
	public TestSourceAssetLocation(RootNode rootNode, AssetContainer assetContainer, File dir, AssetLocation... dependentAssetLocations)
	{
		super(rootNode, assetContainer, dir, dependentAssetLocations);
	}
	
	protected AssetLocation createNewAssetLocationForChildDir(File dir, AssetLocation parentAssetLocation)
	{
		return new ChildTestSourceAssetLocation(assetContainer().root(), assetContainer(), dir, parentAssetLocation);
	}
}
