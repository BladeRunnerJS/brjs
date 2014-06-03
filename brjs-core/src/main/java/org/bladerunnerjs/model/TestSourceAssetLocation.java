package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;


public final class TestSourceAssetLocation extends AbstractSourceAssetLocation
{
	public TestSourceAssetLocation(RootNode rootNode, Node parent, File dir, AssetLocation... dependentAssetLocations)
	{
		super(rootNode, parent, dir, dependentAssetLocations);
	}
	
	protected AssetLocation createNewAssetLocationForChildDir(File dir, AssetLocation parentAssetLocation)
	{
		return new ChildTestSourceAssetLocation(assetContainer().root(), assetContainer(), dir, parentAssetLocation);
	}
}
