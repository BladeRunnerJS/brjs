package org.bladerunnerjs.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.AbstractBundlableNode;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.SdkJsLib;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.NameValidator;


public class TestPack extends AbstractBundlableNode implements NamedNode
{
	private String name;
	private AssetContainer parentAssetContainer;
	
	public TestPack(RootNode rootNode, Node parent, MemoizedFile dir)
	{
		this(rootNode, parent, dir, dir.getName());
	}
	
	public TestPack(RootNode rootNode, Node parent, MemoizedFile dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
	}
	
	@Override
	public MemoizedFile[] memoizedScopeFiles() {
		List<MemoizedFile> scopeFiles = new ArrayList<>(Arrays.asList(getParentAssetContainer().memoizedScopeFiles()));
		scopeFiles.add(dir());
		
		return scopeFiles.toArray(new MemoizedFile[scopeFiles.size()]);
	}
	
	@Override
	public List<LinkedAsset> seedAssets() 
	{		
		return new ArrayList<>( assetDiscoveryResult().getRegisteredSeedAssets() );
	}
	
	@Override
	public String requirePrefix()
	{
		return getParentAssetContainer().requirePrefix();
	}
	
	@Override
	public boolean isNamespaceEnforced() {
		if (getParentAssetContainer() instanceof SdkJsLib) {
			return false;
		}
		return getParentAssetContainer().isNamespaceEnforced();
	}
	
	@Override
	public List<AssetContainer> scopeAssetContainers()
	{
		List<AssetContainer> assetContainers = new ArrayList<>(getParentAssetContainer().scopeAssetContainers());
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
		if (parentNode() instanceof TypedTestPack) {
			String testTechName = ((TypedTestPack) (parentNode())).getName();
			return getParentAssetContainer().getTypeName().toLowerCase() + "-test-" + testTechName + "-" + name;
			
		}
		return getParentAssetContainer().getTypeName().toLowerCase() + "-" + name;
	}

	private AssetContainer getParentAssetContainer() {
		if (parentAssetContainer == null) {
			parentAssetContainer = rootNode.locateAncestorNodeOfClass(parentNode(), AssetContainer.class);
		}
		return parentAssetContainer;
	}
	
}
