package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.utility.NameValidator;


public class TypedTestPack extends SourceResources implements NamedNode
{
	private final NodeList<TestPack> technologyTestPacks = new NodeList<>(this, TestPack.class, "", null);
	private String name;
	
	public TypedTestPack(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
		
		// TODO: we should never call registerInitializedNode() from a non-final class
	}
	
	public static NodeList<TypedTestPack> createNodeSet(Node node)
	{
		return new NodeList<>(node, TypedTestPack.class, "tests", "^test-");
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
	
	public List<TestPack> testTechs()
	{
		return technologyTestPacks.list();
	}

	public TestPack testTech(String technologyName)
	{
		return technologyTestPacks.item(technologyName);
	}
}
