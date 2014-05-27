package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public abstract class AbstractDeepAssetLocation extends TheAbstractAssetLocation {
	public AbstractDeepAssetLocation(RootNode rootNode, Node parent, File dir, AssetLocation... dependentAssetLocations) {
		super(rootNode, parent, dir, dependentAssetLocations);
	}
	
	@Override
	protected List<File> getCandidateFiles() {
		return getDirInfo().nestedFiles();
	}
}
