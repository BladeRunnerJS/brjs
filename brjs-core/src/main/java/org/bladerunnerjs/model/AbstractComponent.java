package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.api.TestType;
import org.bladerunnerjs.api.TestableNode;
import org.bladerunnerjs.api.TypedTestPack;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.TestRunner;


public abstract class AbstractComponent extends AbstractAssetContainer implements TestableNode
{
	private final NodeList<TypedTestPack> testTypes = TypedTestPack.createNodeSet(this, TypedTestPack.class);
	
	public AbstractComponent(RootNode rootNode, Node parent, MemoizedFile dir) {
		super(rootNode, parent, dir);
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
