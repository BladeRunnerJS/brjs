package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.utility.TestRunner;


public class Workbench extends AbstractBundlableNode implements TestableNode 
{
	private static final List<String> seedFilenames = Arrays.asList("index.html", "index.jsp");
	
	private final NodeItem<DirNode> styleResources = new NodeItem<>(DirNode.class, "resources/style");
	private final NodeMap<TypedTestPack> testTypes = TypedTestPack.createNodeSet();
	private final NodeMap<Theme> themes = Theme.createNodeSet();
	private AssetLocation thisAssetLocation;
	
	public Workbench(RootNode rootNode, Node parent, File dir)
	{
		super(rootNode, dir);
		thisAssetLocation = new ShallowAssetLocation(rootNode, this, dir);
		init(rootNode, parent, dir);
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
	public List<LinkedAssetFile> getSeedFiles() {
		List<LinkedAssetFile> assetFiles = new ArrayList<LinkedAssetFile>();
		
		for (LinkedAssetFile assetFile : thisAssetLocation.seedResources())
		{
			if ( seedFilenames.contains( assetFile.getUnderlyingFile().getName() ) )
			{
				assetFiles.add(assetFile);
			}
		}
		
		return assetFiles;
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
