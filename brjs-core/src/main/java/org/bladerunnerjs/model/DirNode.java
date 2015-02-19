package org.bladerunnerjs.model;

import java.util.Map;

import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;


public final class DirNode extends AbstractBRJSNode
{
	public DirNode(RootNode rootNode, Node parent, MemoizedFile dir)
	{
		super(rootNode, parent, dir);
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
	}
}
