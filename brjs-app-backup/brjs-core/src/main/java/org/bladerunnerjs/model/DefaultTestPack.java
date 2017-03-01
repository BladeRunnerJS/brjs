package org.bladerunnerjs.model;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;


public class DefaultTestPack extends TestPack
{

	public DefaultTestPack(RootNode rootNode, Node parent, MemoizedFile dir)
	{
		super(rootNode, parent, dir, "default");
	}

	@Override
	public void create() throws InvalidNameException, ModelUpdateException
	{
		super.create();
	}
	
	@Override
	public String getTypeName() {
		return getClass().getSuperclass().getSimpleName();
	}
	
}
