package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.apache.commons.lang3.text.WordUtils;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.plugin.utility.IndexPageSeedLocator;
import org.bladerunnerjs.utility.NameValidator;
import org.bladerunnerjs.utility.TestRunner;


public final class Aspect extends AbstractBrowsableNode implements TestableNode, NamedNode
{
	private final NodeList<TypedTestPack> testTypes = TypedTestPack.createNodeSet(this);
	private String name;
	private File[] scopeFiles;
	private IndexPageSeedLocator indexPageSeedLocator;
	
	public Aspect(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
		
		indexPageSeedLocator = new IndexPageSeedLocator(root());
	}
	
	@Override
	public File[] memoizedScopeFiles() {
		if(scopeFiles == null) {
			scopeFiles = new File[] {app().dir(), root().sdkLibsDir().dir(), root().file("js-patches"), BladerunnerConf.getConfigFilePath(root()), app().file("app.conf")};
		}
		
		return scopeFiles;
	}
	
	@Override
	public List<LinkedAsset> modelSeedAssets() {
		return indexPageSeedLocator.seedAssets(this);
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
		transformations.put("aspectTitle", WordUtils.capitalize(getName()) );
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
	public List<AssetContainer> scopeAssetContainers() {
		List<AssetContainer> assetContainers = new ArrayList<>();
		
		assetContainers.add(this);
		
		for(Bladeset bladeset : parent().bladesets()) {
			assetContainers.add(bladeset);
			
			for(Blade blade : bladeset.blades()) {
				assetContainers.add(blade);				
			}
		}
		
		for (JsLib jsLib : parent().jsLibs())
		{
			assetContainers.add( jsLib );			
		}
		
		return assetContainers;
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
		return testTypes.list();
	}
	
	@Override
	public TypedTestPack testType(String typedTestPackName)
	{
		return testTypes.item(typedTestPackName);
	}
	
	
}
