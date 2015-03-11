package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;

public class StandardBundleSet implements BundleSet {
	private final List<LinkedAsset> seedAssets;
	private final List<Asset> assets;
	private final List<LinkedAsset> linkedAssets;
	private final List<SourceModule> sourceModules;
	private BundlableNode bundlableNode;
	
	public StandardBundleSet(BundlableNode bundlableNode, List<LinkedAsset> seedAssets, List<Asset> assets, List<SourceModule> sourceModules) {
		this.seedAssets = seedAssets;
		this.bundlableNode = bundlableNode;
		this.assets = assets;
		
		linkedAssets = new ArrayList<>();
		for (Asset asset : assets) {
			if (asset instanceof LinkedAsset) {
				linkedAssets.add((LinkedAsset) asset);
			}
		}
		this.sourceModules = sourceModules;
	}
	
	@Override
	public BundlableNode getBundlableNode() {
		return bundlableNode;
	}
	
	@Override
	public List<SourceModule> getSourceModules(String... prefixes) {
		return getAssets(sourceModules, Arrays.asList(prefixes), Collections.emptyList());
	}

	@Override
	public List<Asset> getAssets(String... prefixes)
	{
		return getAssets(assets, Arrays.asList(prefixes), Collections.emptyList());
	}
	
	@Override
	public List<Asset> getAssets(List<String> prefixes, List<Class<? extends Asset>> assetTypes)
	{
		return getAssets(assets, prefixes, assetTypes);
	}

	private <AT extends Asset> List<AT> getAssets(List<AT> assets, List<String> prefixes, List<Class<? extends Asset>> assetTypes) {
		List<AT> assetsWithRequirePath = new ArrayList<>();
		for (AT asset : assets) { 
			if (assetHasValidPrefix(asset, prefixes) && assetHasValidType(asset, assetTypes)) {
				assetsWithRequirePath.add(asset);
			}
		}
		return assetsWithRequirePath;
	}
	
	private boolean assetHasValidPrefix(Asset asset, List<String> prefixes)
	{
		if (prefixes.size() == 0) {
			return true;
		}
		for (String prefix : prefixes) {
			if (asset.getPrimaryRequirePath().startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean assetHasValidType(Asset asset, List<Class<? extends Asset>> assetTypes)
	{
		if (assetTypes.size() == 0) {
			return true;
		}
		for (Class<? extends Asset> assetType : assetTypes) {
			if (asset.getClass().isAssignableFrom(assetType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<LinkedAsset> seedAssets()
	{
		return seedAssets;
	}

	@Override
	public List<LinkedAsset> getLinkedAssets()
	{
		return linkedAssets;
	}
	
}
