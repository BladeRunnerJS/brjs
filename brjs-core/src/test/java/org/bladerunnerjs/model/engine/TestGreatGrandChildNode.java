package org.bladerunnerjs.model.engine;

import java.io.File;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.AbstractNode;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.utility.NameValidator;


public class TestGreatGrandChildNode extends AbstractNode implements NamedNode
{
	private String name;
	
	public TestGreatGrandChildNode(RootNode rootNode, Node parent, File dir, String name)
	{
		this.name = name;
		init(rootNode, parent, dir);
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
}
