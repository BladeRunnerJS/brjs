package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public class ChildSourceAssetLocation extends ShallowAssetLocation {
	private List<AssetLocation> dependentAssetLocations = new ArrayList<>();
	
	public ChildSourceAssetLocation(RootNode rootNode, Node parent, File dir, AssetLocation parentAssetLocation) {
		super(rootNode, parent, dir);
		dependentAssetLocations.add(parentAssetLocation);
	}
	
	@Override
	public List<AssetLocation> getDependentAssetLocations() {
		return dependentAssetLocations;
	}
}
