package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.aliasing.AliasDefinition;

public class StandardBundleSet implements BundleSet {
	private final List<Asset> assets;
	private final List<SourceModule> sourceModules;
	private final List<AliasDefinition> activeAliases;
	private BundlableNode bundlableNode;
	
	public StandardBundleSet(BundlableNode bundlableNode, List<Asset> assets, List<SourceModule> sourceModules, List<AliasDefinition> activeAliases) {
		this.bundlableNode = bundlableNode;
		this.assets = assets;
		this.sourceModules = sourceModules;
		this.activeAliases = activeAliases;
	}
	
	@Override
	public BundlableNode getBundlableNode() {
		return bundlableNode;
	}
	
	@Override
	public List<Asset> getAssets() {
		return assets;
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
	public List<Asset> getAssetsWithRequirePrefix(String... prefixes)
	{
		List<Asset> assetsWithRequirePath = new ArrayList<>();
		for (Asset asset : assets) { 
			for (String prefix : prefixes) {
				if (asset.getPrimaryRequirePath().startsWith(prefix)) {
					assetsWithRequirePath.addAll(assets);
					break;
				}
			}
		}
		return assetsWithRequirePath;
	}
	
}
