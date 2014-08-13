package org.bladerunnerjs.model;

import java.io.File;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;


public class DefaultAspect extends Aspect
{

	public DefaultAspect(RootNode rootNode, Node parent, File dir)
	{
		super(rootNode, parent, dir, "default");
	}
	
	@Override
	public void create() throws InvalidNameException, ModelUpdateException
	{
		super.createDefaultNode();
	}
	
	@Override
	public void populate() throws InvalidNameException, ModelUpdateException
	{
		BRJSNodeHelper.populate(this, true);
	}

	public boolean exists()
	{
		return file("index.html").isFile();
	}

	@Override
	public String getTypeName() {
		return super.getClass().getSimpleName();
	}
	
}
