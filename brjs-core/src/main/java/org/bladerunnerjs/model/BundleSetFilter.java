package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.aliasing.AliasDefinition;

public class BundleSetFilter {
	public List<SourceModule> filterSourceModules(List<SourceModule> sourceModules) {
		return sourceModules;
	}
	
	public List<AliasDefinition> filterActiveAliases(List<AliasDefinition> activeAliases) {
		return activeAliases;
	}
	
	public List<AssetLocation> filterResourceNodes(List<AssetLocation> resourceLocations) {
		return resourceLocations;
	}
}
