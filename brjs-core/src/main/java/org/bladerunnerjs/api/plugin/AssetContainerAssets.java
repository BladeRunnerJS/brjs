package org.bladerunnerjs.api.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.model.AssetContainer;


public class AssetContainerAssets
{
	private final MemoizedValue<AssetDiscoveryResult> assetDiscoveryResult;
	private final List<AssetLocationPlugin> assetLocationPlugins;
	private AssetContainer assetContainer;
	
	private List<Asset> implicitDependencies = new ArrayList<>();
	
	public AssetContainerAssets(AssetContainer assetContainer)
	{
		this.assetContainer = assetContainer;
		assetLocationPlugins = assetContainer.root().plugins().assetLocationPlugins();
		assetDiscoveryResult = new MemoizedValue<>("AssetContainerAssets.assetDiscoveryResult", assetContainer);
	}
	
	public Collection<Asset> assets() {
		return assetDiscoveryResult().assets.values();
	}
	
	public Collection<Asset> seedAssets() {
		return assetDiscoveryResult().seedAssets.values();
	}
	
	public boolean hasRegisteredAsset(String requirePath)
	{
		return assetDiscoveryResult().assets.containsKey(requirePath);
	}
	
	public Asset getRegisteredAsset(String requirePath)
	{
		return assetDiscoveryResult().assets.get(requirePath);
	}
	
	private AssetDiscoveryResult assetDiscoveryResult() {
		return assetDiscoveryResult.value(() -> {
			return new AssetDiscoveryResult();
		});
	}
	
	
	private class AssetDiscoveryResult implements AssetDiscoveryInitiator {
		
		private final Map<String,Asset> assets = new HashMap<>();
		private final Map<String,Asset> seedAssets = new HashMap<>();
		private boolean furtherDiscoveryRequired = true;
		
		private AssetDiscoveryResult() {
			//TODO: do we need the loop?
			while (furtherDiscoveryRequired) {
				furtherDiscoveryRequired = false;
				discoverFurtherAssets(assetContainer.dir(), assetContainer.requirePrefix(), implicitDependencies);
			}
		}
		
		@Override
		public void registerSeedAsset(Asset asset)
		{
			registerAsset(asset);
			seedAssets.put(asset.getPrimaryRequirePath(), asset);
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
		
	}
	
}
