package org.bladerunnerjs.api.plugin;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetContainer;


public class DefaultAssetDiscoveryInitiator implements AssetDiscoveryInitiator
{

	private List<SourceModule> sourceModules;
	private List<LinkedAsset> linkedAssets;
	private AssetContainer assetContainer;
	private boolean furtherDiscoveryRequired = true;
	
	private List<Asset> implicitDependencies = new ArrayList<>();
	
	public DefaultAssetDiscoveryInitiator(AssetContainer assetContainer)
	{
		this.assetContainer = assetContainer;
	}
	
	public List<SourceModule> discoveredSourceModules()
	{
		doAssetDiscovery();
		return sourceModules;
	}

	public List<LinkedAsset> discoveredLinkedAssets()
	{
		doAssetDiscovery();
		return linkedAssets;
	}
	
	
	@Override
	public void registerAsset(Asset asset)
	{
		furtherDiscoveryRequired = true;
	}

	@Override
	public void discoverFurtherAssets(MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies)
	{
		
	}
	
	
	private void doAssetDiscovery()
	{
		List<AssetLocationPlugin> assetLocationPlugins = assetContainer.root().plugins().assetLocationPlugins();
		sourceModules = new ArrayList<>();
		linkedAssets = new ArrayList<>();
		
		while (furtherDiscoveryRequired) {
			furtherDiscoveryRequired = false;
			for (AssetLocationPlugin assetLocationPlugin : assetLocationPlugins) {
				assetLocationPlugin.discoverAssets(assetContainer, assetContainer.dir(), assetContainer.requirePrefix(), implicitDependencies, this);
			}
		}
	}

}
