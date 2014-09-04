package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.engine.RootNode;


public final class ChildTestSourceAssetLocation extends AbstractChildSourceAssetLocation implements TestAssetLocation
{
	public ChildTestSourceAssetLocation(RootNode rootNode, AssetContainer assetContainer, File dir, AssetLocation parentAssetLocation)
	{
		super(rootNode, assetContainer, dir, parentAssetLocation, parentAssetLocation);
	}
}
