package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.RootNode;

public abstract class AbstractDeepAssetLocation extends TheAbstractAssetLocation {
	public AbstractDeepAssetLocation(RootNode rootNode, AssetContainer assetContainer, MemoizedFile dir, AssetLocation parentAssetLocation, AssetLocation... dependentAssetLocations) {
		super(rootNode, assetContainer, dir, parentAssetLocation, dependentAssetLocations);
	}
	
	@Override
	public List<MemoizedFile> getCandidateFiles() {
		return rootNode.getMemoizedFile(dir()).nestedFiles();
	}
}
