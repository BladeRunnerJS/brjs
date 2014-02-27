package org.bladerunnerjs.model;

import java.io.File;
import java.util.Map;

import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;

public final class WorkingDirNode extends AbstractBRJSNode {
	public WorkingDirNode(RootNode rootNode, File dir) {
		super(rootNode, rootNode, dir);
		
		// working directory nodes aren't centrally registered at this point
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException {
	}
}
