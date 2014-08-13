package org.bladerunnerjs.model;

import java.io.File;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.utility.NameValidator;


public final class Theme extends AbstractBRJSNode implements NamedNode
{
	private String name;
	
	public Theme(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
	}
	
	public static NodeList<Theme> createNodeSet(Node node)
	{
		return new NodeList<>(node, Theme.class, "themes", null);
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
