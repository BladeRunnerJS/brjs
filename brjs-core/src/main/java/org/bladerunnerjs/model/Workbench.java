package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
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
	public Workbench(RootNode rootNode, Node parent, File dir)
	{
		super(rootNode, parent, dir);
		this.parent = (N) parent;
		seedLocator = new IndexPageSeedLocator(root());
	}
	
	@Override
	public File[] memoizedScopeFiles() {
		List<File> scopeFiles = new ArrayList<>(Arrays.asList(app().memoizedScopeFiles()));
		scopeFiles.add(dir());
		return scopeFiles.toArray(new File[scopeFiles.size()]);
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
		return getClass().getSuperclass().getSimpleName();
	}
}
