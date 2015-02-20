package org.bladerunnerjs.model;

import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.RootNode;

public abstract class SourceResources extends AbstractBRJSNode
{
	private final NodeItem<DirNode> i18nResources = new NodeItem<>(this, DirNode.class, "resources/i18n");
	
	public SourceResources(RootNode rootNode, Node parent, MemoizedFile dir) {
		super(rootNode, parent, dir);
	}
	
	public DirNode i18nResources()
	{
		return i18nResources.item();
	}
}
