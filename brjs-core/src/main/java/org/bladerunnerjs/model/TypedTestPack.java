package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.utility.NameValidator;


public class TypedTestPack extends SourceResources implements NamedNode
{
	private final NodeList<TestPack> technologyTestPacks = new NodeList<>(this, TestPack.class, null, "");
	private final NodeItem<TestPack> defaultTestPack = new NodeItem<>(this, TestPack.class, ".");
	private String name;
	
	public TypedTestPack(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
	}
	
	public static NodeList<TypedTestPack> createNodeSet(Node node)
	{
		if (node.file("tests").isDirectory()) {
			return new NodeList<>(node, TypedTestPack.class, "tests", "^test-");			
		}
		return new NodeList<>(node, TypedTestPack.class, ".", "^test-");			
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
		List<TestPack> testTechs = new ArrayList<>();
		if (hasSingleDefaultTestTech()) {
			testTechs.add( defaultTestTech() );
		} else {
			testTechs.addAll( technologyTestPacks.list() );
		}
		return testTechs;
	}

	public TestPack testTech(String technologyName)
	{
		if (technologyName.equals(App.DEFAULT_CONTAINER_NAME)) {
			return defaultTestTech();
		}
		return technologyTestPacks.item(technologyName);
	}
	
	public TestPack defaultTestTech()
	{
		return defaultTestPack.item();
	}
	
	private boolean hasSingleDefaultTestTech() {
		for (File file : rootNode.getFileInfo(dir()).filesAndDirs()) {
			if (file.getName().equals("tests") || file.getName().endsWith(".conf")) {
				return true;
			}
		}
		return false;
	}
	
}
