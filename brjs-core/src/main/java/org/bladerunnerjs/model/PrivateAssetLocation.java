package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public class PrivateAssetLocation extends ShallowAssetLocation {
	public PrivateAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}
	
	@Override
	protected void registerNode() {
		// do nothing: private asset locations shouldn't be locatable via BRJS
	}
}
