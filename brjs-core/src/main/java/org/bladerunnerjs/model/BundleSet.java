package org.bladerunnerjs.model;

import java.util.List;

public class BundleSet {
	private final List<SourceFile> sourceFiles;
	private final List<AliasDefinition> activeAliases;
	private final List<Resources> resources;
	
	public BundleSet(List<SourceFile> sourceFiles, List<AliasDefinition> activeAliases, List<Resources> resources) {
		this.sourceFiles = sourceFiles;
		this.activeAliases = activeAliases;
		this.resources = resources;
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
