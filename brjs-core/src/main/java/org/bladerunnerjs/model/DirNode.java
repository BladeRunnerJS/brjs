package org.bladerunnerjs.model;

import java.util.Map;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;


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
