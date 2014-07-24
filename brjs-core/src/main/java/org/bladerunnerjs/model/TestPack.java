package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.utility.NameValidator;


public class TestPack extends AbstractBundlableNode implements NamedNode
{
	private AliasesFile aliasesFile;
	private String name;
	
	public TestPack(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
	}
	
	@Override
	public File[] memoizedScopeFiles() {
		List<File> scopeFiles = new ArrayList<>(Arrays.asList(testScope().memoizedScopeFiles()));
		scopeFiles.add(dir());
		
		return scopeFiles.toArray(new File[scopeFiles.size()]);
	}
	
	@Override
	public List<LinkedAsset> modelSeedAssets() 
	{
		// TODO: add extra coverage so this can be fixed without causing only js breakage
//		return Collections.emptyList();
		
		List<LinkedAsset> seedFiles = new ArrayList<>();
		
		for(AssetLocation assetLocation : assetLocations()) {
			if(assetLocation instanceof TestAssetLocation) {
				seedFiles.addAll(assetLocation.sourceModules());
			}
		}
		
		return seedFiles;
	}
	
	@Override
	public String requirePrefix()
	{
		return "$test/" + testScope().requirePrefix();
	}
	
	@Override
	public boolean isNamespaceEnforced() {
		return false;
	}
	
	@Override
	public List<AssetContainer> scopeAssetContainers()
	{
		List<AssetContainer> assetContainers = new ArrayList<>(testScope().scopeAssetContainers());
		assetContainers.add(this);
		
		return assetContainers;
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
	public String getTemplateName()
	{
		return testScope().getClass().getSimpleName().toLowerCase() + "-" + name;
	}
	
	public AssetContainer testScope() {
		return (AssetContainer) parentNode().parentNode();
	}
	
	public AliasesFile aliasesFile()
	{
		if(aliasesFile == null) {
			aliasesFile = new AliasesFile(dir(), "resources/aliases.xml", this);
		}
		
		return aliasesFile;
	}
	
	public AssetLocation testSource()
	{
		return assetLocation("src-test");
	}
	
	public AssetLocation tests()
	{
		return assetLocation("tests");
	}
	
}
