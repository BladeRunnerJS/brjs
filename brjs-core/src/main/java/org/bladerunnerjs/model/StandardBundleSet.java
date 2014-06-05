package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.plugin.AssetPlugin;

public class StandardBundleSet implements BundleSet {
	private final List<SourceModule> sourceModules;
	private final List<AliasDefinition> activeAliases;
	private final List<AssetLocation> resourceLocations;
	private BundlableNode bundlableNode;
	
	public StandardBundleSet(BundlableNode bundlableNode, List<SourceModule> sourceModules, List<AliasDefinition> activeAliases, List<AssetLocation> resources) {
		this.bundlableNode = bundlableNode;
		this.sourceModules = sourceModules;
		this.activeAliases = activeAliases;
		this.resourceLocations = resources;
	}
	
	@Override
	public BundlableNode getBundlableNode() {
		return bundlableNode;
	}
	
	@Override
	public List<String> getThemes(){
		List<String> result = new ArrayList<String>();
		for(AssetLocation location: resourceLocations){
			if(location instanceof ThemedAssetLocation){
				result.add(((ThemedAssetLocation)location).getThemeName());
			}
		}
		return result;
	}
	
	@Override
	public List<SourceModule> getSourceModules() {
		return sourceModules;
	}
	
	@Override
	public List<AliasDefinition> getActiveAliases() {
		return activeAliases;
	}
	
	@Override
	public List<AssetLocation> getResourceNodes() {
		return resourceLocations;
	}
	
	@Override
	public List<Asset> getResourceFiles(AssetPlugin assetProducer) {
		Set<Asset> resourceFiles = new LinkedHashSet<Asset>();
		
		for(AssetLocation resourceNode : resourceLocations) {
			resourceFiles.addAll(resourceNode.bundlableAssets(assetProducer));
		}
		
		List<Asset> result = new ArrayList<Asset>();
		result.addAll(resourceFiles);
		
		return result;
	}
	
}
