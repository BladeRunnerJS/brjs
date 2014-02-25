package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.plugin.AssetPlugin;

public class BundleSet {
	private final List<SourceModule> sourceModules;
	private final List<TestSourceModule> testSourceModules;
	private final List<AliasDefinition> activeAliases;
	private final List<AssetLocation> resourceLocations;
	private BundlableNode bundlableNode;
	
	public BundleSet(BundlableNode bundlableNode, List<SourceModule> sourceModules, List<AliasDefinition> activeAliases, List<AssetLocation> resources) {
		this.bundlableNode = bundlableNode;
		this.sourceModules = new LinkedList<SourceModule>();;
		this.testSourceModules = new LinkedList<TestSourceModule>();
		for (SourceModule sourceModule : sourceModules)
		{
			if (sourceModule instanceof TestSourceModule)
			{
				testSourceModules.add((TestSourceModule) sourceModule);
			}
			else
			{
				this.sourceModules.add(sourceModule);				
			}
		}
		this.activeAliases = activeAliases;
		this.resourceLocations = resources;
	}
	
	public BundlableNode getBundlableNode() {
		return bundlableNode;
	}
	
	public List<SourceModule> getSourceModules() {
		return sourceModules;
	}
	
	public List<TestSourceModule> getTestSourceModules() {
		return testSourceModules;
	}
	
	public List<AliasDefinition> getActiveAliases() {
		return activeAliases;
	}
	
	public List<AssetLocation> getResourceNodes() {
		return resourceLocations;
	}
	
	public List<Asset> getResourceFiles(String fileExtension) {
		Set<Asset> resourceFiles = new LinkedHashSet<Asset>();
		
		for(AssetLocation resourceNode : resourceLocations) {
			resourceFiles.addAll(resourceNode.bundleResources(fileExtension));
		}
		
		List<Asset> result = new ArrayList<Asset>();
		result.addAll(resourceFiles);
		
		return orderAssetsBasedOnAssetContainer(result);
	}
	
	public List<Asset> getResourceFiles(AssetPlugin assetProducer) {
		Set<Asset> resourceFiles = new LinkedHashSet<Asset>();
		
		for(AssetLocation resourceNode : resourceLocations) {
			resourceFiles.addAll(resourceNode.bundleResources(assetProducer));
		}
		
		List<Asset> result = new ArrayList<Asset>();
		result.addAll(resourceFiles);
		
		return orderAssetsBasedOnAssetContainer(result);
	}
	
	private <A extends Asset> List<A> orderAssetsBasedOnAssetContainer(List<A> assets)
	{
		List<A> assetsNotOrderedByContainer = new ArrayList<A>(assets);
		List<A> orderedAssets = new LinkedList<A>();
		
		orderedAssets.addAll( getAssetsInContainer(assetsNotOrderedByContainer, Blade.class) );
		orderedAssets.addAll( getAssetsInContainer(assetsNotOrderedByContainer, Bladeset.class) );
		orderedAssets.addAll( getAssetsInContainer(assetsNotOrderedByContainer, Aspect.class) );
		orderedAssets.addAll( getAssetsInContainer(assetsNotOrderedByContainer, Workbench.class) );
		
		orderedAssets.addAll(0, assetsNotOrderedByContainer);
		
		return orderedAssets;
	}
	
	private <A extends Asset> List<A> getAssetsInContainer(List<A> assets, Class<? extends AssetContainer> assetContainerType)
	{
		List<A> assetsInContainer = new ArrayList<A>();
		
		for (A asset : assets)
		{
			if (asset.getAssetLocation().getAssetContainer().getClass() == assetContainerType)
			{
				assetsInContainer.add(asset);
			}
		}
		assets.removeAll(assetsInContainer);
		return assetsInContainer;
	}
	
}
