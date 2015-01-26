package org.bladerunnerjs.model;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.TemplateUtility;


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
		
		TemplateUtility.populateOrCreate(testType("unit").defaultTestTech(), templateGroup);
		TemplateUtility.populateOrCreate(testType("acceptance").defaultTestTech(), templateGroup);
	}

	public boolean exists()
	{
		return file("index.html").isFile() || file("index.jsp").isFile();
	}

	@Override
	public String getTypeName() {
		return getClass().getSuperclass().getSimpleName();
	}
	
}
