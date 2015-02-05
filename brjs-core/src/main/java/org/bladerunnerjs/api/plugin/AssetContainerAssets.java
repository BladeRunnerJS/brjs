package org.bladerunnerjs.api.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetFileInstantationException;


public class AssetContainerAssets implements AssetDiscoveryInitiator
{

	private final List<AssetLocationPlugin> assetLocationPlugins;
	
//	private Map<String,Asset> assets = new HashMap<>();
	private List<Asset> assets = new ArrayList<>();
	
	private AssetContainer assetContainer;
	private boolean furtherDiscoveryRequired;
	
	private List<Asset> implicitDependencies = new ArrayList<>();
	
	public AssetContainerAssets(AssetContainer assetContainer)
	{
		this.assetContainer = assetContainer;
		assetLocationPlugins = assetContainer.root().plugins().assetLocationPlugins();
	}
	
	@Override
	public void registerAsset(Asset asset)
	{
//		String assetPrimaryRequirePath = asset.getPrimaryRequirePath();
//		if (assets.containsKey(assetPrimaryRequirePath) ) {
//			throw new RuntimeException("Asset with require path '"+assetPrimaryRequirePath+"' already registered");
//		}
//		assets.put(assetPrimaryRequirePath, asset);
		assets.add(asset);
//		furtherDiscoveryRequired = true;
	}

	@Override
	public void discoverFurtherAssets(MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies)
	{
		
		for (AssetLocationPlugin assetLocationPlugin : assetLocationPlugins) {
			assetLocationPlugin.discoverAssets(assetContainer, dir, requirePrefix, implicitDependencies, this);
		}
	}

	public Collection<Asset> discoverAssets()
	{
		assets.clear();
		furtherDiscoveryRequired = true;				
		
		while (furtherDiscoveryRequired) {
			furtherDiscoveryRequired = false;
			discoverFurtherAssets(assetContainer.dir(), assetContainer.requirePrefix(), implicitDependencies);
		}
			
//		return assets.values();
		return assets;
	}
	
}
