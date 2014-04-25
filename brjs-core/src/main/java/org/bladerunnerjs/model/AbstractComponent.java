package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.engine.ThemeableNode;
import org.bladerunnerjs.utility.TestRunner;


public abstract class AbstractComponent extends AbstractAssetContainer implements TestableNode, ThemeableNode
{
	private final NodeMap<Theme> themes;
	private final NodeMap<TypedTestPack> testTypes;
	
	public AbstractComponent(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
		themes = Theme.createNodeSet(this);
		testTypes = TypedTestPack.createNodeSet(this);
	}
	
	@Override
	public List<Theme> themes()
	{
		return children(this.themes);
	}
	
	@Override
	public Theme theme(String name)
	{
		return child(this.themes, name);
	}
	
	@Override
	public void runTests(TestType... testTypes)
	{
		TestRunner.runTests(testTypes);
	}
	
	@Override
	public List<TypedTestPack> testTypes()
	{
		return children(testTypes);
	}
	
	@Override
	public TypedTestPack testType(String testTypeName)
	{
		return child(testTypes, testTypeName);
	}
}
