package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public class SourceAssetLocation extends ShallowAssetLocation {
	public SourceAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}
	
	public List<AssetLocation> getChildAssetLocations() {
		// TODO...
		return new ArrayList<>();
	}
	
	public AssetLocation getChildAssetLocation(File dir) {
		// TODO...
		return null;
	}
}
