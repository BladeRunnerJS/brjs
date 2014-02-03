package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.aliasing.AliasDefinition;

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
	
	public List<Asset> getResourceFiles(String fileExtension) {
		Set<Asset> resourceFiles = new HashSet<Asset>();
		
		for(AssetLocation resourceNode : resourceLocations) {
			resourceFiles.addAll(resourceNode.bundleResources(fileExtension));
		}
		
		List<Asset> result = new ArrayList<Asset>();
		result.addAll(resourceFiles);
		return result;
	}
}
