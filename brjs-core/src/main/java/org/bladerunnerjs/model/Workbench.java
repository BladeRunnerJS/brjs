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
import org.bladerunnerjs.model.utility.TestRunner;


public class Workbench extends AbstractBundlableNode implements TestableNode {
	private final NodeItem<DirNode> styleResources = new NodeItem<>(DirNode.class, "resources/style");
	private final NodeMap<TypedTestPack> testTypes = TypedTestPack.createNodeSet();
	private final NodeMap<Theme> themes = Theme.createNodeSet();
	private final FileSet<LinkedAssetFile> seedFiles;

	public Workbench(RootNode rootNode, Node parent, File dir)
	{
		super(dir);
		init(rootNode, parent, dir);
		
		seedFiles = FileSetBuilder.createLinkedAssetFileSetForDir(this)
			.includingPaths("index.html", "index.jsp", "resources/**.xml")
			.excludingPaths("resources/aliases.xml")
			.build();
	}

	public DirNode styleResources()
	{
		return item(styleResources);
	}
		
	public Blade parent()
	{
		return (Blade) parent;
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
	}
	
	@Override
	public String getRequirePrefix() {
		App app = parent().parent().parent();
		return "/" + app.getNamespace();
	}
	
	@Override
	public List<LinkedAssetFile> getSeedFiles() {
		return seedFiles.getFiles();
	}
	
	@Override
	public List<SourceLocation> getSourceLocations() {
		List<SourceLocation> sourceLocations = new ArrayList<>();
		
		sourceLocations.add(this);
		sourceLocations.add(this.parent());
		sourceLocations.add(this.parent().parent());
		
		for(JsLib jsLib : parent().parent().parent().jsLibs()) {
			sourceLocations.add(jsLib);
		}
		
		return sourceLocations;
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
