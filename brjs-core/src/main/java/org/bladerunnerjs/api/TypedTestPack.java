package org.bladerunnerjs.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.DefaultTestPack;
import org.bladerunnerjs.model.SourceResources;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.DuplicateAssetContainerException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.utility.NameValidator;


public class TypedTestPack extends SourceResources implements NamedNode
{
	private final NodeList<TestPack> technologyTestPacks = new NodeList<>(this, TestPack.class, null, "");
	private final NodeItem<DefaultTestPack> defaultTestPack = new NodeItem<>(this, DefaultTestPack.class, ".");
	private String name;
	
	public TypedTestPack(RootNode rootNode, Node parent, MemoizedFile dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
	}
	
	public static <T extends TypedTestPack> NodeList<T> createNodeSet(Node node, Class<T> nodeListClass)
	{
		if (node.file("tests").isDirectory()) {
			return new NodeList<T>(node, nodeListClass, "tests", "^test-");			
		}
		return new NodeList<T>(node, nodeListClass, ".", "^test-");
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
		if (hasSingleDefaultTestTech()) {
			throw new DuplicateAssetContainerException("The test pack at '%s' directly contains test configuration and therefore should not contain sub test tech nodes, yet a named test tech was requested.", 
					root().dir().getRelativePath(dir()) );
		}
		return technologyTestPacks.item(technologyName);
	}
	
	public TestPack defaultTestTech()
	{
		if (!hasSingleDefaultTestTech() && !technologyTestPacks.list().isEmpty()) {
			throw new DuplicateAssetContainerException("The test pack at '%s' contains test tech nodes and therefore should not contain a 'deafult' test tech, yet the default test tech node was requested.", 
					root().dir().getRelativePath(dir()) );
		}
		return defaultTestPack.item();
	}
	
	private boolean hasSingleDefaultTestTech() {
		for (File file : root().getMemoizedFile(dir()).filesAndDirs()) {
			if (file.getName().equals("tests") || file.getName().endsWith(".conf")) {
				return true;
			}
		}
		return false;
	}
	
}
