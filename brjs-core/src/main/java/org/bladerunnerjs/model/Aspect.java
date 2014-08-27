package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.utility.IndexPageSeedLocator;
import org.bladerunnerjs.utility.NameValidator;
import org.bladerunnerjs.utility.TestRunner;


public class Aspect extends AbstractBrowsableNode implements TestableNode, NamedNode
{
	private final NodeList<TypedTestPack> testTypes = TypedTestPack.createNodeSet(this, TypedTestPack.class);
	private String name;
	private File[] scopeFiles;
	private IndexPageSeedLocator indexPageSeedLocator;
	
	public Aspect(RootNode rootNode, Node parent, File dir) {
		this(rootNode, parent, dir, StringUtils.substringBeforeLast(dir.getName(), "-aspect"));
	}
	
	public Aspect(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
		
		indexPageSeedLocator = new IndexPageSeedLocator(root());
	}
	
	@Override
	public File[] memoizedScopeFiles() {
		if(scopeFiles == null) {
			scopeFiles = new File[] {app().dir(), root().sdkJsLibsDir().dir(), root().file("js-patches"), BladerunnerConf.getConfigFilePath(root()), app().file("app.conf")};
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
		transformations.put("requirePrefix", requirePrefix());
		transformations.put("namespace", requirePrefix().replace("/", "."));
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
		boolean isValidName = NameValidator.isValidDirectoryName(name);
		boolean matchesLocaleFormat = name.matches(Locale.LANGUAGE_AND_COUNTRY_CODE_FORMAT);
		return isValidName && !matchesLocaleFormat;
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
		testType("unit").defaultTestTech().populate();
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
		
		for (JsLib jsLib : parent().jsLibs())
		{
			assetContainers.add( jsLib );			
		}
		
		for(Bladeset bladeset : parent().bladesets()) {			
			for(Blade blade : bladeset.blades()) {
				assetContainers.add(blade);				
			}
			assetContainers.add(bladeset);
		}
		
		assetContainers.add(this);
		
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
