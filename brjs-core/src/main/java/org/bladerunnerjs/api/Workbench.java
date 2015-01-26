package org.bladerunnerjs.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.AbstractBrowsableNode;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.plugin.utility.IndexPageSeedLocator;
import org.bladerunnerjs.utility.TestRunner;

public abstract class Workbench<N extends Node> extends AbstractBrowsableNode implements TestableNode
{
	private final NodeItem<DirNode> styleResources = new NodeItem<>(this, DirNode.class, "resources/style");
	private final NodeList<TypedTestPack> testTypes = TypedTestPack.createNodeSet(this, TypedTestPack.class);
	private final IndexPageSeedLocator seedLocator;
	private final N parent;
	
	// TODO add type checking
	@SuppressWarnings("unchecked")
	public Workbench(RootNode rootNode, Node parent, MemoizedFile dir)
	{
		super(rootNode, parent, dir);
		this.parent = (N) parent;
		seedLocator = new IndexPageSeedLocator(root());
	}
	
	@Override
	public MemoizedFile[] memoizedScopeFiles() {
		List<MemoizedFile> scopeFiles = new ArrayList<>(Arrays.asList(app().memoizedScopeFiles()));
		scopeFiles.add(dir());
		return scopeFiles.toArray(new MemoizedFile[0]);
	}

	public DirNode styleResources()
	{
		return styleResources.item();
	}
	
	public N parent() {
		return parent;
	}
	
	@Override
	public List<LinkedAsset> modelSeedAssets() {
		return seedLocator.seedAssets(this);
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
	}
	
	@Override
	public String requirePrefix() {
		return app().getRequirePrefix();
	}
	
	@Override
	public boolean isNamespaceEnforced() {
		return false;
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
	public TypedTestPack testType(String typedTestPackName)
	{
		return testTypes.item(typedTestPackName);
	}
	
	@Override
	public String getTypeName() {
		return getClass().getSimpleName();
	}
}
