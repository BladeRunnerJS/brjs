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


public class TypedTestPack extends SourceResources implements NamedNode
{
	private final NodeMap<TestPack> technologyTestPacks = TestPack.createNodeSet();
	private String name;
	
	public TypedTestPack(RootNode rootNode, Node parent, File dir, String name)
	{
		this.name = name;
		init(rootNode, parent, dir);
	}
	
	public static NodeMap<TypedTestPack> createNodeSet()
	{
		return new NodeMap<>(TypedTestPack.class, "tests", "^test-");
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
	
	public List<TestPack> testTechs()
	{
		return children(technologyTestPacks);
	}

	public TestPack testTech(String technologyName)
	{
		return child(technologyTestPacks, technologyName);
	}
}
