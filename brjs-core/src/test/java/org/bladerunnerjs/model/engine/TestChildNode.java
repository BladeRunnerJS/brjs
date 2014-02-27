package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.List;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.AbstractNode;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.NameValidator;


public final class TestChildNode extends AbstractNode implements NamedNode
{
	NodeMap<TestGrandChildNode> grandChildNodes;
	private String name;
	
	public TestChildNode(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
		grandChildNodes = new NodeMap<>(rootNode, TestGrandChildNode.class, "grandchild", null);
		
		registerInitializedNode();
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public boolean isValidName()
	{
		return NameValidator.isValidDirectoryName(name);
	}
	
	@Override
	public void assertValidName() throws InvalidNameException
	{
		NameValidator.assertValidDirectoryName(this);
	}
	
	public List<TestGrandChildNode> grandChildNodes()
	{
		return children(grandChildNodes);
	}
	
	public TestGrandChildNode grandChildNode(String appName)
	{
		return child(grandChildNodes, appName);
	}
}
