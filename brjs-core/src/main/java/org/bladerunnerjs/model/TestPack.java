package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.utility.NameValidator;


public class TestPack extends AbstractBundlableNode implements NamedNode
{
	private AliasesFile aliasesFile;
	private String name;
	private final MemoizedValue<Set<SourceModule>> sourceModulesList = new MemoizedValue<>("TestPack.sourceModules", root(), dir(), root().conf().file("bladerunner.conf"));
	
	public TestPack(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
		
		// TODO: we should never call registerInitializedNode() from a non-final class
		registerInitializedNode();
	}
	
	@Override
	public File[] scopeFiles() {
		List<File> scopeFiles = new ArrayList<>(Arrays.asList(testScope().scopeFiles()));
		scopeFiles.add(dir());
		
		return scopeFiles.toArray(new File[scopeFiles.size()]);
	}
	
	@Override
	public List<LinkedAsset> modelSeedAssets() 
	{
		return new ArrayList<>();
	}
	
	@Override
	public String requirePrefix()
	{
		return "";
	}
	
	@Override
	public boolean isNamespaceEnforced() {
		return false;
	}
	
	@Override
	public List<AssetContainer> assetContainers()
	{
		List<AssetContainer> assetContainers = new ArrayList<>(testScope().scopeAssetContainers());
		assetContainers.add(this);
		
		return assetContainers;
	}
	
	@Override
	public Set<SourceModule> sourceModules() {
		return sourceModulesList.value(() -> {
			Set<SourceModule> sourceModules = new LinkedHashSet<SourceModule>();
			
			for (AssetLocation assetLocation : assetLocations())
			{
				if ( !isTestAssetLocation(assetLocation) )
				{
					sourceModules.addAll(assetLocation.sourceModules());
				}
			}
			
			return sourceModules;
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
	
	private boolean isTestAssetLocation(AssetLocation assetLocation)
	{
		return assetLocation instanceof TestSourceAssetLocation || assetLocation instanceof ChildTestSourceAssetLocation;
	}
	
}
