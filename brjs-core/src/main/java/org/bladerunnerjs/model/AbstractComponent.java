package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.TestRunner;


public abstract class AbstractComponent extends AbstractAssetContainer implements TestableNode
{
	private final NodeMap<Theme> themes = Theme.createNodeSet();
	private final NodeMap<TypedTestPack> testTypes = TypedTestPack.createNodeSet();
	
	public AbstractComponent(RootNode rootNode, File dir) {
		super(rootNode, dir);
	}
	
	public List<Theme> themes()
	{
		return children(this.themes);
	}
	
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
