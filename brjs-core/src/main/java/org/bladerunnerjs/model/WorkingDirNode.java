package org.bladerunnerjs.model;

import java.util.Map;

import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.engine.RootNode;

public final class WorkingDirNode extends AbstractBRJSNode {
	public WorkingDirNode(RootNode rootNode, MemoizedFile dir) {
		super(rootNode, rootNode, dir);
		
		// working directory nodes aren't centrally registered at this point
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException {
	}
}
