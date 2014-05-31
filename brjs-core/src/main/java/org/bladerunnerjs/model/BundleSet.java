package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
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
	
	
	public List<String> getThemes(){
		List<String> result = new ArrayList<String>();
		for(AssetLocation location: resourceLocations){
			if(location instanceof ThemedAssetLocation){
				result.add(((ThemedAssetLocation)location).getThemeName());
			}
		}
		return result;
	}
	
	public ThemedAssetLocation getThemedResourceLocation(String themeName){
		
		ThemedAssetLocation result = null;
		for(AssetLocation location: resourceLocations){
			if(location instanceof ThemedAssetLocation){
				String locationThemeName = ((ThemedAssetLocation)location).getThemeName();
				if(locationThemeName.equals(themeName)){
					result = ((ThemedAssetLocation)location);
				}
			}
		}
		return result;
	}
	
	
	public List<String> getFile(String themeName, String resourcePath){
		List<String> result = new ArrayList<String>();
		for(AssetLocation location: resourceLocations){
			if(location instanceof ThemedAssetLocation){
				result.add(((ThemedAssetLocation)location).getThemeName());
			}
		}
		return result;
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
		
		return result;
	}
	
}
