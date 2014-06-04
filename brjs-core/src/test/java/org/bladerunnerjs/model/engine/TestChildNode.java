package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.List;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.AbstractNode;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.NameValidator;


public final class TestChildNode extends AbstractNode implements NamedNode
{
	NodeList<TestGrandChildNode> grandChildNodes;
	private String name;
	
	public TestChildNode(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
		grandChildNodes = new NodeList<>(this, TestGrandChildNode.class, "grandchild", null);
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
		return grandChildNodes.list();
	}
	
	public TestGrandChildNode grandChildNode(String appName)
	{
		return grandChildNodes.item(appName);
	}
}
