package org.bladerunnerjs.model;

import java.util.List;

public class BundleSet {
	private final List<SourceFile> sourceFiles;
	private final List<AliasDefinition> activeAliases;
	private final List<Resources> resources;
	private BundlableNode bundlableNode;
	
	public BundleSet(BundlableNode bundlableNode, List<SourceFile> sourceFiles, List<AliasDefinition> activeAliases, List<Resources> resources) {
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
	
	public List<Resources> getResourceNodes() {
		return resources;
	}
}
