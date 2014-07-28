package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.engine.RootNode;

public final class ChildSourceAssetLocation extends AbstractChildSourceAssetLocation {
	public ChildSourceAssetLocation(RootNode rootNode, AssetContainer assetContainer, File dir, AssetLocation parentAssetLocation, AssetLocation... dependentAssetLocations) {
		super(rootNode, assetContainer, dir, parentAssetLocation, dependentAssetLocations);
	}
}
