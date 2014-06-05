package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.plugin.AssetPlugin;

public class FilteredBundleSet implements BundleSet {
	private BundleSet bundleSet;
	private BundleSetFilter bundleSetFilter;
	
	public FilteredBundleSet(BundleSet bundleSet, BundleSetFilter bundleSetFilter) {
		this.bundleSet = bundleSet;
		this.bundleSetFilter = bundleSetFilter;
	}
	
	@Override
	public BundlableNode getBundlableNode() {
		return bundleSet.getBundlableNode();
	}
	
	@Override
	public List<Asset> getResourceFiles(AssetPlugin assetProducer) {
		return bundleSet.getResourceFiles(assetProducer);
	}
	
	@Override
	public List<SourceModule> getSourceModules() {
		return bundleSetFilter.filterSourceModules(bundleSet.getSourceModules());
	}
	
	@Override
	public List<AliasDefinition> getActiveAliases() {
		return bundleSetFilter.filterActiveAliases(bundleSet.getActiveAliases());
	}
	
	@Override
	public List<AssetLocation> getResourceNodes() {
		return bundleSetFilter.filterResourceNodes(bundleSet.getResourceNodes());
	}
}
