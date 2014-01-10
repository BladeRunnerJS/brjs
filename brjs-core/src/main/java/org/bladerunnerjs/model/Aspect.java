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
import org.bladerunnerjs.utility.IndexPageSeedFileLocator;
import org.bladerunnerjs.utility.NameValidator;
import org.bladerunnerjs.utility.TestRunner;


public class Aspect extends AbstractBundlableNode implements TestableNode, NamedNode
{
	private final NodeItem<DirNode> unbundledResources = new NodeItem<>(DirNode.class, "unbundled-resources");
	private final NodeMap<TypedTestPack> testTypes;
	private final NodeMap<Theme> themes;
	private String name;
	
	public Aspect(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
		testTypes = TypedTestPack.createNodeSet(rootNode);
		themes = Theme.createNodeSet(rootNode);
	}
	
	public static NodeMap<Aspect> createNodeSet(RootNode rootNode)
	{
		return new NodeMap<>(rootNode, Aspect.class, null, "-aspect$");
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
	public String namespace() {
		App app = parent();
		return app.getNamespace();
	}
	
	@Override
	public List<AssetContainer> getAssetContainers() {
		List<AssetContainer> assetContainers = new ArrayList<>();
		
		assetContainers.add(this);
		assetContainers.addAll(parent().getNonAspectAssetContainers());
		
		return assetContainers;
	}
	
	public App parent()
	{
		return (App) parentNode();
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
