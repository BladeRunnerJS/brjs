package org.bladerunnerjs.api.plugin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.model.AssetContainer;


public class AssetContainerAssets
{
	private final MemoizedValue<DefaultAssetDiscoveryInitiator> assetDiscoveryResult;
	private final List<AssetPlugin> assetPlugins;
	private AssetContainer assetContainer;
	
	private List<Asset> implicitDependencies = new ArrayList<>();
	
	public AssetContainerAssets(AssetContainer assetContainer)
	{
		this.assetContainer = assetContainer;
		assetPlugins = assetContainer.root().plugins().assetPlugins();
		assetDiscoveryResult = new MemoizedValue<>("AssetContainerAssets.assetDiscoveryResult", assetContainer);
	}
	
	public Set<Asset> assets() {
		return new LinkedHashSet<Asset>( assetDiscoveryResult().assets.values() );
	}
	
	public Map<String,Asset> assetsMap() {
		return new LinkedHashMap<>( assetDiscoveryResult().assets );
	}
	
	public List<LinkedAsset> seedAssets() {
		return new ArrayList<>( assetDiscoveryResult().seedAssets );
	}
	
	public boolean hasRegisteredAsset(String requirePath)
	{
		return assetDiscoveryResult().assets.containsKey(requirePath);
	}
	
	public Asset getRegisteredAsset(String requirePath)
	{
		return assetDiscoveryResult().assets.get(requirePath);
	}
	
	private DefaultAssetDiscoveryInitiator assetDiscoveryResult() {
		return assetDiscoveryResult.value(() -> {
			return new DefaultAssetDiscoveryInitiator();
		});
	}
	
	
	private class DefaultAssetDiscoveryInitiator implements AssetDiscoveryInitiator {
		
		private final Map<String,Asset> assets = new LinkedHashMap<>();
		private final List<LinkedAsset> seedAssets = new ArrayList<>();
		
		private DefaultAssetDiscoveryInitiator() {
			discoverFurtherAssets(assetContainer.dir(), assetContainer.requirePrefix(), implicitDependencies);
		}
		
		@Override
		public void registerSeedAsset(LinkedAsset asset)
		{
			registerAsset(asset);
			seedAssets.add(asset);
		}
		
		@Override
		public void registerAsset(Asset asset)
		{
			for (String requirePath : asset.getRequirePaths()) {
				if (assets.containsKey(requirePath) ) {
					throw new RuntimeException(
							String.format("Require paths for the asset '%s' cannot be registered. An asset for the require path '%s' has already been registered.", asset.getAssetPath(), requirePath)
					);
				}
				assets.put(requirePath, asset);				
			}
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
		public List<Asset> discoverFurtherAssets(MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies)
		{
			List<Asset> discoveredAssets = new ArrayList<>();
			for (AssetPlugin assetPlugin : assetPlugins) {
				discoveredAssets.addAll( assetPlugin.discoverAssets(assetContainer, dir, requirePrefix, implicitDependencies, this) );
			}
			return discoveredAssets;
		}
		
	}
	
}
