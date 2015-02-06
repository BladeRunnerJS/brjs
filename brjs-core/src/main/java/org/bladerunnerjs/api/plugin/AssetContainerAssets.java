package org.bladerunnerjs.api.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetContainer;


public class AssetContainerAssets implements AssetDiscoveryInitiator
{

	private final List<AssetLocationPlugin> assetLocationPlugins;
	
	private Map<String,Asset> assets = new HashMap<>();
	
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
		String assetPrimaryRequirePath = asset.getPrimaryRequirePath();
		if (assets.containsKey(assetPrimaryRequirePath) ) {
			throw new RuntimeException("An asset for the require path '"+assetPrimaryRequirePath+"' has already been registered.");
		}
		assets.put(assetPrimaryRequirePath, asset);
		furtherDiscoveryRequired = true;
	}

	@Override
	public boolean hasRegisteredAsset(String requirePath)
	{
		return assets.containsKey(requirePath);
	}
	
	@Override
	public Asset getRegisteredAsset(String requirePath)
	{
		return assets.get(requirePath);
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
			
		return assets.values();
	}
	
}
