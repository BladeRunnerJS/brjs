package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public final class SourceAssetLocation extends AbstractSourceAssetLocation {
	
	public SourceAssetLocation(RootNode rootNode, Node parent, File dir, AssetLocation... dependentAssetLocations) {
		super(rootNode, parent, dir, dependentAssetLocations);
		
		// TODO: understand why removing this line doesn't break any tests
		registerInitializedNode();
	}
	
	public SourceAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}
	
	@Override
	protected List<File> getCandidateFiles() {
		return dirInfo.files();
	}
	
}
