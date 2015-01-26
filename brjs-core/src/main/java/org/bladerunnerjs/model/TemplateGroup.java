package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;

public class TemplateGroup extends AbstractBRJSNode {

	private final NodeList<NamedDirNode> templatesInConf = new NodeList<>(this, NamedDirNode.class, ".", null);
	
	public TemplateGroup(RootNode rootNode, Node parent, MemoizedFile dir) {
		super(rootNode, parent, dir);
	}
	
	public List<NamedDirNode> templates()
	{
		List<NamedDirNode> templates = new ArrayList<>(templatesInConf.list());
		return templates;
	}
	
	public NamedDirNode template(String templateName)
	{
		return templatesInConf.item(templateName);
	}

	@Override
	public void addTemplateTransformations(Map<String, String> transformations)
			throws ModelUpdateException {
		// do nothing
	}
}
