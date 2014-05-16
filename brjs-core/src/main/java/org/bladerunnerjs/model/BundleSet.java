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
	private final List<AliasDefinition> activeAliases;
	private final List<AssetLocation> resourceLocations;
	private BundlableNode bundlableNode;
	
	public BundleSet(BundlableNode bundlableNode, List<SourceModule> sourceModules, List<AliasDefinition> activeAliases, List<AssetLocation> resources) {
		this.bundlableNode = bundlableNode;
		this.sourceModules = sourceModules;
		this.activeAliases = activeAliases;
		this.resourceLocations = resources;
	}
	
	public BundlableNode getBundlableNode() {
		return bundlableNode;
	}
	
	public List<SourceModule> getSourceModules() {
		return sourceModules;
	}
	
	public List<AliasDefinition> getActiveAliases() {
		return activeAliases;
	}
	
	public List<AssetLocation> getResourceNodes() {
		return resourceLocations;
	}
	
	public List<Asset> getResourceFiles(AssetPlugin assetProducer) {
		Set<Asset> resourceFiles = new LinkedHashSet<Asset>();
		
		for(AssetLocation resourceNode : resourceLocations) {
			resourceFiles.addAll(resourceNode.bundlableAssets(assetProducer));
		}
		
		List<Asset> result = new ArrayList<Asset>();
		result.addAll(resourceFiles);
		
		return orderAssets(bundlableNode, result);
	}
	
	private <A extends Asset> List<A> orderAssets(BundlableNode bundlableNode, List<A> assets)
	{
		List<A> assetsNotOrderedByContainer = new ArrayList<A>(assets);
		List<A> orderedAssets = new LinkedList<A>();
		
		for (AssetContainer assetContainer : bundlableNode.scopeAssetContainers())
		{
			orderedAssets.addAll( 0, getAssetsInContainer(assetsNotOrderedByContainer, assetContainer) );
		}
		
		orderedAssets.addAll(0, assetsNotOrderedByContainer);
		
		return orderedAssets;
	}
	
	private <A extends Asset> List<A> getAssetsInContainer(List<A> assets, AssetContainer assetContainer)
	{
		List<A> assetsInContainer = new ArrayList<A>();
		
		for (A asset : assets)
		{
			if (asset.assetLocation().assetContainer() == assetContainer)
			{
				assetsInContainer.add(asset);
			}
		}
		assets.removeAll(assetsInContainer);
		return assetsInContainer;
	}
	
}
