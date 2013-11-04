package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.utility.NameValidator;
import org.bladerunnerjs.model.utility.TestRunner;


public class Aspect extends AbstractBundlableNode implements TestableNode, NamedNode
{
	private final NodeItem<DirNode> unbundledResources = new NodeItem<>(DirNode.class, "unbundled-resources");
	private final NodeMap<TypedTestPack> testTypes = TypedTestPack.createNodeSet();
	private final NodeMap<Theme> themes = Theme.createNodeSet();
	private String name;
	
	public Aspect(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, dir);
		this.name = name;
		init(rootNode, parent, dir);
	}
	
	public static NodeMap<Aspect> createNodeSet()
	{
		return new NodeMap<>(Aspect.class, null, "-aspect$");
	}
	
	@Override
	public FileSet<LinkedAssetFile> getSeedFileSet() {
		return new StandardFileSet<LinkedAssetFile>(this, StandardFileSet.paths("index.html",  "index.jsp"), StandardFileSet.paths(), null);
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public boolean isValidName()
	{
		return NameValidator.isValidDirectoryName(name);
	}
	
	@Override
	public void assertValidName() throws InvalidNameException
	{
		NameValidator.assertValidDirectoryName(this);
	}
	
	@Override
	public void populate() throws InvalidNameException, ModelUpdateException
	{
		super.populate();
		theme("standard").populate();
	}
	
	@Override
	public String getRequirePrefix() {
		App app = parent();
		return "/" + app.getNamespace();
	}
	
	@Override
	public List<SourceLocation> getSourceLocations() {
		List<SourceLocation> sourceLocations = new ArrayList<>();
		
		sourceLocations.add(this);
		
		for(JsLib jsLib : parent().jsLibs()) {
			sourceLocations.add(jsLib);
		}
		
		for(Bladeset bladeset : parent().bladesets()) {
			sourceLocations.add(bladeset);
			
			for(Blade blade : bladeset.blades()) {
				sourceLocations.add(blade);
			}
		}
		
		return sourceLocations;
	}
	
	public App parent()
	{
		return (App) parent;
	}
	
	public DirNode unbundledResources()
	{
		return item(unbundledResources);
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
