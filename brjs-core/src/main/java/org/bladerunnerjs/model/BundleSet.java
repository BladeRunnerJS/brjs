package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.aliasing.AliasDefinition;

public class BundleSet {
	private final List<SourceModule> sourceModules;
	private final List<AliasDefinition> activeAliases;
	private final List<AssetLocation> resources;
	private BundlableNode bundlableNode;
	
	public BundleSet(BundlableNode bundlableNode, List<SourceModule> sourceModules, List<AliasDefinition> activeAliases, List<AssetLocation> resources) {
		this.bundlableNode = bundlableNode;
		this.sourceModules = sourceModules;
		this.activeAliases = activeAliases;
		this.resources = resources;
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
		return resources;
	}
	
	public List<Asset> getResourceFiles(String fileExtension) {
		List<Asset> resourceFiles = new ArrayList<>();
		
		for(AssetLocation resourceNode : resources) {
			resourceFiles.addAll(resourceNode.bundleResources(fileExtension));
		}
		
		return resourceFiles;
	}
}
