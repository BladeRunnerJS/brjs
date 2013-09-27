package org.bladerunnerjs.model;

import java.io.File;
import java.util.Map;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;


public class DirNode extends AbstractBRJSNode
{
	public DirNode(RootNode rootNode, Node parent, File dir)
	{
		init(rootNode, parent, dir);
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
	}
}
