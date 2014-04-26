package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.engine.ThemeableNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.utility.IndexPageSeedFileLocator;
import org.bladerunnerjs.utility.NameValidator;
import org.bladerunnerjs.utility.TestRunner;


public final class Aspect extends AbstractBrowsableNode implements TestableNode, NamedNode, ThemeableNode
{
	private final NodeList<TypedTestPack> testTypes;
	private final NodeList<Theme> themes;
	private String name;
	private File[] scopeFiles;
	
	private final MemoizedValue<List<LinkedAsset>> seedFileList = new MemoizedValue<>("Aspect.seedFiles", root(), dir(), root().conf().file("bladerunner.conf"));
	private final MemoizedValue<List<AssetContainer>> assetContainerList = new MemoizedValue<>("Aspect.assetContainer", this);
	private final MemoizedValue<List<TypedTestPack>> testTypesList = new MemoizedValue<>("Aspect.testTypes", root(), file("tests"));
	private final MemoizedValue<List<Theme>> themesList = new MemoizedValue<>("Aspect.themes", root(), file("themes"));
	
	public Aspect(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
		testTypes = TypedTestPack.createNodeSet(this);
		themes = Theme.createNodeSet(this);
		
		registerInitializedNode();
	}
	
	public static NodeList<Aspect> createNodeSet(Node node)
	{
		return new NodeList<>(node, Aspect.class, null, "-aspect$");
	}
	
	@Override
	public File[] scopeFiles() {
		if(scopeFiles == null) {
			scopeFiles = new File[] {app().dir(), root().libsDir(), root().file("js-patches"), root().conf().file("bladerunner.conf")};
		}
		
		return scopeFiles;
	}
	
	@Override
	public List<LinkedAsset> getSeedFiles() {
		return seedFileList.value(() -> {
			return IndexPageSeedFileLocator.getSeedFiles(this);
		});
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
	public String requirePrefix() {
		return parent().getRequirePrefix();
	}
	
	@Override
	public boolean isNamespaceEnforced() {
		return false;
	}
	
	@Override
	public List<AssetContainer> assetContainers() {
		return assetContainerList.value(() -> {
			List<AssetContainer> assetContainers = new ArrayList<>();
			
			assetContainers.add(this);
			assetContainers.addAll(parent().getNonAspectAssetContainers());
			
			return assetContainers;
		});
	}
	
	public App parent()
	{
		return (App) parentNode();
	}
	
	@Override
	public void runTests(TestType... testTypes)
	{
		TestRunner.runTests(testTypes);
	}
	
	@Override
	public List<TypedTestPack> testTypes()
	{
		return testTypesList.value(() -> {
			return testTypes.list();
		});
	}
	
	@Override
	public TypedTestPack testType(String typedTestPackName)
	{
		return testTypes.item(typedTestPackName);
	}
	
	@Override
	public List<Theme> themes()
	{
		return themesList.value(() -> {
			return themes.list();
		});
	}
	
	@Override
	public Theme theme(String themeName)
	{
		return themes.item(themeName);
	}
}
