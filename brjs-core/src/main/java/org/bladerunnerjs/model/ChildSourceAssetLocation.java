package org.bladerunnerjs.model;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.RootNode;

public final class ChildSourceAssetLocation extends AbstractChildSourceAssetLocation {
	public ChildSourceAssetLocation(RootNode rootNode, AssetContainer assetContainer, MemoizedFile dir, AssetLocation parentAssetLocation, AssetLocation... dependentAssetLocations) {
		super(rootNode, assetContainer, dir, parentAssetLocation, dependentAssetLocations);
	}
}
