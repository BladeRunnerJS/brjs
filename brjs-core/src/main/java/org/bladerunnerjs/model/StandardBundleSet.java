package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private Map<List<Class<? extends Asset>>, List<Asset>> assetsByType = new HashMap<>();
	private Map<List<Class<? extends SourceModule>>, List<SourceModule>> sourceModulesByType = new HashMap<>();
	
	public StandardBundleSet(BundlableNode bundlableNode, List<LinkedAsset> seedAssets, List<Asset> assets, List<LinkedAsset> linkedAssets, List<SourceModule> sourceModules) {
		this.seedAssets = seedAssets;
		this.bundlableNode = bundlableNode;
		this.assets = assets;
		this.linkedAssets = linkedAssets;
		this.sourceModules = sourceModules;
	}
	
	@Override
	public BundlableNode getBundlableNode() {
		return bundlableNode;
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
	
	@SuppressWarnings("unchecked")
	@Override
	public <AT extends SourceModule> List<AT> getSourceModules(Class<? extends AT> assetType) {
		return (List<AT>) getTheAssets(sourceModules, sourceModulesByType, null, Arrays.asList(assetType));
	}
	
	@Override
	public List<SourceModule> getSourceModules(List<Class<? extends SourceModule>> assetTypes) {
		return getTheAssets(sourceModules, sourceModulesByType, null, assetTypes);
	}

	@Override
	public List<Asset> getAssets(String... prefixes)
	{
		return (List<Asset>) getTheAssets(assets, assetsByType, Arrays.asList(prefixes), null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <AT extends Asset> List<AT> getAssets(Class<? extends AT> assetType, String... prefixes)
	{
		return (List<AT>) getTheAssets(assets, assetsByType, Arrays.asList(prefixes), Arrays.asList(assetType));
	}
	
	@Override
	public List<Asset> getAssets(List<Class<? extends Asset>> assetTypes, String... prefixes)
	{
		return (List<Asset>) getTheAssets(assets, assetsByType, Arrays.asList(prefixes), assetTypes);
	}

	
	
	
	private static <AT extends Asset> List<AT> getTheAssets(List<AT> assets, Map<List<Class<? extends AT>>, List<AT>> assetsByType, 
			List<String> prefixes, List<Class<? extends AT>> assetTypes) {
		
		List<AT> assetsOfCorrectType = getAssetsForType(assets, assetsByType, assetTypes);
		
		if (prefixes == null || prefixes.isEmpty()) {
			return assetsOfCorrectType;
		}
		
		List<AT> assetsWithRequirePath = new ArrayList<>();
		for (AT asset : assetsOfCorrectType) { 
			if (assetHasValidPrefix(asset, prefixes)) {
				assetsWithRequirePath.add(asset );
			}
		}
		
		return assetsWithRequirePath;
	}
	
	private static <AT extends Asset> List<AT> getAssetsForType(List<AT> assets, Map<List<Class<? extends AT>>, List<AT>> assetsByType, List<Class<? extends AT>> assetTypes) {
		if (assetTypes == null || assetTypes.isEmpty()) {
			return assets;
		}
		
		List<AT> assetsForAssetsTypes = getAssetsForType(assetsByType, assetTypes);
		if (assetsForAssetsTypes == null) {
			assetsForAssetsTypes = new ArrayList<>();
			for (AT asset : assets) {
				if (assetHasValidType(asset, assetTypes)) {
					assetsForAssetsTypes.add(asset);
				}
			}
			
			assetsByType.put(assetTypes, assetsForAssetsTypes);
		}
		
		return new ArrayList<>(assetsForAssetsTypes);
	}
	
	private static <AT extends Asset> List<AT> getAssetsForType(Map<List<Class<? extends AT>>, List<AT>> assetsByType, List<Class<? extends AT>> assetTypes) {
		for (List<Class<? extends AT>> assetTypesFromMap : assetsByType.keySet()) {
			List<Class<? extends AT>> assetTypesToFind = new ArrayList<>(assetTypes);
			for (Class<? extends AT> assetTypeToMatch : assetTypesFromMap) {
				assetTypesToFind.remove(assetTypeToMatch);
				if (assetTypesToFind.isEmpty() && assetTypes.size() == assetTypesFromMap.size()) {
					return assetsByType.get(assetTypesFromMap);
				}
			}
		}
		return null;
	}
	
	private static <AT extends Asset> boolean assetHasValidPrefix(AT asset, List<String> prefixes)
	{
		for (String prefix : prefixes) {
			if (asset.getPrimaryRequirePath().startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}
	
	private static <AT extends Asset> boolean assetHasValidType(AT asset, List<Class<? extends AT>> assetTypes)
	{
		for (Class<? extends Asset> assetType : assetTypes) {
			if (assetType.isAssignableFrom(asset.getClass())) {
				return true;
			}
		}
		return false;
	}
	
}
