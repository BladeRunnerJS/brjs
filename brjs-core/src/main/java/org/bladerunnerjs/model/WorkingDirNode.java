package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.engine.RootNode;

public class WorkingDirNode extends DirNode {
	public WorkingDirNode(RootNode rootNode, File dir) {
		super(rootNode, rootNode, dir);
	}
	
	@Override
	protected void registerNode() {
	}
}
