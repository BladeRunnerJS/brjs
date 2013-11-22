package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.aliasing.AliasDefinition;

public class BundleSet {
	private final List<SourceFile> sourceFiles;
	private final List<AliasDefinition> activeAliases;
	private final List<AssetLocation> resources;
	private BundlableNode bundlableNode;
	
	public BundleSet(BundlableNode bundlableNode, List<SourceFile> sourceFiles, List<AliasDefinition> activeAliases, List<AssetLocation> resources) {
		this.bundlableNode = bundlableNode;
		this.sourceFiles = sourceFiles;
		this.activeAliases = activeAliases;
		this.resources = resources;
	}
	
	public BundlableNode getBundlableNode() {
		return bundlableNode;
	}
	
	public List<SourceFile> getSourceFiles() {
		return sourceFiles;
	}
	
	public List<AliasDefinition> getActiveAliases() {
		return activeAliases;
	}
	
	public List<AssetLocation> getResourceNodes() {
		return resources;
	}
	
	public List<AssetFile> getResourceFiles(String fileExtension) {
		List<AssetFile> resourceFiles = new ArrayList<>();
		
		for(AssetLocation resourceNode : resources) {
			resourceFiles.addAll(resourceNode.bundleResources(fileExtension));
		}
		
		return resourceFiles;
	}
}
