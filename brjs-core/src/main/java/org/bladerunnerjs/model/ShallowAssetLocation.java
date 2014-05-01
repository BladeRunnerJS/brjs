package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;


public final class ShallowAssetLocation extends AbstractAssetLocation {
	public ShallowAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
		
		// TODO: understand why removing this line doesn't break any tests
		registerInitializedNode();
	}
	
	@Override
	protected List<File> getCandidateFiles() {
		return dirInfo.files();
	}
}
