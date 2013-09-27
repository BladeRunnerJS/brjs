package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.utility.NameValidator;


public class JsNonBladeRunnerLib extends AbstractBRJSNode implements NamedNode
{
	private String name;
	
	public JsNonBladeRunnerLib(RootNode rootNode, Node parent, File dir)
	{
		init(rootNode, parent, dir);
	}
	
	public static NodeMap<JsNonBladeRunnerLib> createSdkNonBladeRunnerLibNodeSet()
	{
		return new NodeMap<>(JsNonBladeRunnerLib.class, "sdk/libs/javascript/thirdparty", null);
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
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
	
	public List<JsNonBladeRunnerLib> getDependencies()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
