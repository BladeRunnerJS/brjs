package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public final class PrivateAssetLocation extends AbstractShallowAssetLocation {
	public PrivateAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
		
		// private asset locations aren't centrally registered at this point
	}
	
	@Override
	protected List<File> getCandidateFiles() {
		return dirInfo.files();
	}
}
