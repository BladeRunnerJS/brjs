package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public final class ChildSourceAssetLocation extends AbstractChildSourceAssetLocation {
	
	public ChildSourceAssetLocation(RootNode rootNode, Node parent, File dir, AssetLocation parentAssetLocation) {
		super(rootNode, parent, dir, parentAssetLocation);
		
		// TODO: understand why removing this line doesn't break any tests
		registerInitializedNode();
	}
	
	@Override
	protected List<File> getCandidateFiles() {
		return dirInfo.files();
	}
	
}
