package org.bladerunnerjs.model;

import java.io.File;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;


public class DefaultTestPack extends TestPack
{

	public DefaultTestPack(RootNode rootNode, Node parent, File dir)
	{
		super(rootNode, parent, dir, "test-default");
	}

	@Override
	public void create() throws InvalidNameException, ModelUpdateException
	{
		super.createDefaultNode();
	}
	
	@Override
	public String getTypeName() {
		return super.getTypeName();
	}
	
}
