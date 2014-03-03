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


public final class Workbench extends AbstractBrowsableNode implements TestableNode 
{
	private final NodeItem<DirNode> styleResources = new NodeItem<>(DirNode.class, "resources/style");
	private final NodeMap<TypedTestPack> testTypes;
	private final NodeMap<Theme> themes;
	
	public Workbench(RootNode rootNode, Node parent, File dir)
	{
		super(rootNode, parent, dir);
		testTypes = TypedTestPack.createNodeSet(rootNode);
		themes = Theme.createNodeSet(rootNode);
		
		registerInitializedNode();
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
		// TODO: Aspect and Workbench shouldn't define a namespace so that i18n tokens and aliases can be overridden, but we should create some tests before we make this change
		return getApp().getNamespace();
	}
	
	@Override
	public List<AssetContainer> getAssetContainers() {
		List<AssetContainer> assetContainers = new ArrayList<>();
		
		assetContainers.add( getApp().aspect("default") );
		
		assetContainers.add( this );
		assetContainers.add( root().locateAncestorNodeOfClass(this, Blade.class) );
		assetContainers.add( root().locateAncestorNodeOfClass(this, Bladeset.class) );
		
		for(JsLib jsLib : getApp().jsLibs()) {
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
