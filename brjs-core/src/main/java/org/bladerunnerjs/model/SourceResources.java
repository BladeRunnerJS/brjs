package org.bladerunnerjs.model;

import org.bladerunnerjs.model.engine.NodeItem;

public abstract class SourceResources extends AbstractBRJSNode
{
	private final NodeItem<DirNode> i18nResources = new NodeItem<>(DirNode.class, "resources/i18n");
	
	public DirNode i18nResources()
	{
		return item(i18nResources);
	}
}
