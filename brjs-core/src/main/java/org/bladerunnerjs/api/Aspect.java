package org.bladerunnerjs.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.model.AbstractBrowsableNode;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.plugin.utility.IndexPageSeedLocator;
import org.bladerunnerjs.utility.AspectRequestHandler;
import org.bladerunnerjs.utility.NameValidator;
import org.bladerunnerjs.utility.TemplateUtility;
import org.bladerunnerjs.utility.TestRunner;


public class Aspect extends AbstractBrowsableNode implements TestableNode, NamedNode
{
	private final NodeList<TypedTestPack> testTypes = TypedTestPack.createNodeSet(this, TypedTestPack.class);
	private String name;
	private MemoizedFile[] scopeFiles;
	private IndexPageSeedLocator indexPageSeedLocator;
	private AspectRequestHandler aspectRequestHandler;
	
	public Aspect(RootNode rootNode, Node parent, MemoizedFile dir) {
		this(rootNode, parent, dir, StringUtils.substringBeforeLast(dir.getName(), "-aspect"));
	}
	
	public Aspect(RootNode rootNode, Node parent, MemoizedFile dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
		
		indexPageSeedLocator = new IndexPageSeedLocator(root());
		this.aspectRequestHandler = new AspectRequestHandler(this);
	}
	
	@Override
	public MemoizedFile[] memoizedScopeFiles() {
		if(scopeFiles == null) {
			scopeFiles = new MemoizedFile[] {app().dir(), root().sdkJsLibsDir().dir(), root().file("js-patches")};
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
		transformations.put("aspectRequirePrefix", requirePrefix());
		transformations.put("aspectNamespace", requirePrefix().replace("/", "."));
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
	public void populate(String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException
	{
		super.populate(templateGroup);
		
		TemplateUtility.populateOrCreate(testType("unit").defaultTestTech(), templateGroup);
		TemplateUtility.populateOrCreate(testType("acceptance").defaultTestTech(), templateGroup);
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
	
	public AspectRequestHandler requestHandler() {
		return aspectRequestHandler;
	}
	
}
