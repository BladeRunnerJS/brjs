package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	private Map<Class<? extends Asset>, List<Asset>> assetsByType = new HashMap<>();
	
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
	public List<SourceModule> getSourceModules() {
		return getTheAssets(sourceModules, null, null);
	}
	
	@Override
	public <AT extends SourceModule> List<AT> getSourceModules(List<Class<? extends AT>> assetTypes) {
		return getTheAssets(sourceModules, null, assetTypes);
	}

	@Override
	public List<Asset> getAssets(String... prefixes)
	{
		return getTheAssets(assets, Arrays.asList(prefixes), null);
	}
	
	@Override
	public <AT extends Asset> List<AT> getAssets(List<String> prefixes, List<Class<? extends AT>> assetTypes)
	{
		return getTheAssets(assets, prefixes, assetTypes);
	}

	@SuppressWarnings("unchecked")
	private <AT extends Asset> List<AT> getTheAssets(List<? extends Asset> assets, List<String> prefixes, List<Class<? extends AT>> assetTypes) {
		List<AT> assetsOfCorrectType = getAssetsForType(assets, assetTypes);
		List<AT> assetsWithRequirePath = new ArrayList<>();
		for (Asset asset : assetsOfCorrectType) { 
			if (assetHasValidPrefix(asset, prefixes)) {
				assetsWithRequirePath.add( (AT) asset );
			}
		}
		return assetsWithRequirePath;
	}
	
	@SuppressWarnings("unchecked")
	private <AT extends Asset> List<AT> getAssetsForType(List<? extends Asset> assets, List<Class<? extends AT>> assetTypes) {
		if (assetTypes == null || assetTypes.isEmpty()) {
			return (List<AT>) assets;
		}
		
		Set<AT> assetsOfType = new LinkedHashSet<>();
		for (Class<? extends AT> assetType : assetTypes) {
			assetsOfType.addAll( getAssetsForType(assets, assetType) );
		}
		
		return new ArrayList<>(assetsOfType);
	}
	
	@SuppressWarnings("unchecked")
	private <AT extends Asset> List<AT> getAssetsForType(List<? extends Asset> assets, Class<? extends AT> assetType) {
		List<Asset> assetsOfType = assetsByType.get(assetType);
		if (assetsOfType == null) {
			assetsOfType = new ArrayList<>();
			for (Asset asset : assets) {
				if (assetType.isAssignableFrom(asset.getClass())) {
					assetsOfType.add(asset);
				}
			}
			assetsByType.put(assetType, assetsOfType);
		}
		return (List<AT>) assetsOfType;
	}
	
	private boolean assetHasValidPrefix(Asset asset, List<String> prefixes)
	{
		if (prefixes == null || prefixes.size() == 0) {
			return true;
		}
		for (String prefix : prefixes) {
			if (asset.getPrimaryRequirePath().startsWith(prefix)) {
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
