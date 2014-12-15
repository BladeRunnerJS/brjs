package org.bladerunnerjs.model;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;


public class DefaultAspect extends Aspect
{

	public DefaultAspect(RootNode rootNode, Node parent, MemoizedFile dir)
	{
		super(rootNode, parent, dir, "default");
	}
	
	@Override
	public void create() throws InvalidNameException, ModelUpdateException
	{
		super.createDefaultNode();
	}
	
	@Override
	public void populate(String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException
	{
		BRJSNodeHelper.populate(this, templateGroup, true);
		
		ImplicitTemplateHandlerUtility templateUtility = new ImplicitTemplateHandlerUtility();
		templateUtility.populateOrCreate(testType("unit").defaultTestTech(), templateGroup);
		templateUtility.populateOrCreate(testType("acceptance").defaultTestTech(), templateGroup);
	}

	public boolean exists()
	{
		return file("index.html").isFile();
	}

	@Override
	public String getTypeName() {
		return getClass().getSuperclass().getSimpleName();
	}
	
}
