package org.bladerunnerjs.model.engine;

import java.io.File;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.AbstractNode;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.NameValidator;


public final class TestGreatGrandChildNode extends AbstractNode implements NamedNode
{
	private String name;
	
	public TestGreatGrandChildNode(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getTypeName() {
		return this.getClass().getSimpleName();
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
