package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.engine.ThemeableNode;
import org.bladerunnerjs.utility.TestRunner;


public abstract class AbstractComponent extends AbstractAssetContainer implements TestableNode, ThemeableNode
{
	private final NodeList<Theme> themes = Theme.createNodeSet(this);
	private final NodeList<TypedTestPack> testTypes = TypedTestPack.createNodeSet(this);
	
	public AbstractComponent(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}
	
	@Override
	public List<Theme> themes()
	{
		return themes.list();
	}
	
	@Override
	public Theme theme(String name)
	{
		return themes.item(name);
	}
	
	@Override
	public void runTests(TestType... testTypes)
	{
		TestRunner.runTests(testTypes);
	}
	
	@Override
	public List<TypedTestPack> testTypes()
	{
		return testTypes.list();
	}
	
	@Override
	public TypedTestPack testType(String testTypeName)
	{
		return testTypes.item(testTypeName);
	}
}
