package org.bladerunnerjs.model;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;


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
