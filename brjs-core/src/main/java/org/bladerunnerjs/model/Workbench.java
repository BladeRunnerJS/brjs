package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.utility.IndexPageSeedFileLocator;
import org.bladerunnerjs.utility.TestRunner;


public class Workbench extends AbstractBundlableNode implements TestableNode 
{
	private final NodeItem<DirNode> styleResources = new NodeItem<>(DirNode.class, "resources/style");
	private final NodeMap<TypedTestPack> testTypes;
	private final NodeMap<Theme> themes;
	
	public Workbench(RootNode rootNode, Node parent, File dir)
	{
		super(rootNode, parent, dir);
		testTypes = TypedTestPack.createNodeSet(rootNode);
		themes = Theme.createNodeSet(rootNode);
	}

	public DirNode styleResources()
	{
		return item(styleResources);
	}
		
	public Blade parent()
	{
		return (Blade) parentNode();
	}
		
	@Override
	public List<LinkedAsset> getSeedFiles() {
		return IndexPageSeedFileLocator.getSeedFiles(this);
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
	}
	
	@Override
	public String namespace() {
		App app = parent().parent().parent();
		return app.getNamespace();
	}
	
	@Override
	public List<AssetContainer> getAssetContainers() {
		List<AssetContainer> assetContainers = new ArrayList<>();
		
		//TODO: refactor me
		assetContainers.add(this);
		assetContainers.add(this.parent());
		assetContainers.add(this.parent().parent());
		
		for(JsLib jsLib : parent().parent().parent().jsLibs()) {
			assetContainers.add(jsLib);
		}
		
		return assetContainers;
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
	public TypedTestPack testType(String typedTestPackName)
	{
		return child(testTypes, typedTestPackName);
	}
	
	public List<Theme> themes()
	{
		return children(themes);
	}
	
	public Theme theme(String themeName)
	{
		return child(themes, themeName);
	}
}
