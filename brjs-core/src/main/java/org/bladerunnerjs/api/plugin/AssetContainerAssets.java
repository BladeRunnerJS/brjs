package org.bladerunnerjs.api.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.model.AssetContainer;


public class AssetContainerAssets
{
	private final MemoizedValue<AssetDiscoveryResult> assetDiscoveryResult;
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
		return new HashSet<Asset>( assetDiscoveryResult().assets.values() );
	}
	
	public Map<String,Asset> assetsMap() {
		return new HashMap<>( assetDiscoveryResult().assets );
	}
	
	public Map<String,Asset> assetsByPathMap() {
		return new HashMap<>( assetDiscoveryResult().assetsByPath );
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
	
	private AssetDiscoveryResult assetDiscoveryResult() {
		return assetDiscoveryResult.value(() -> {
			return new AssetDiscoveryResult();
		});
	}
	
	
	private class AssetDiscoveryResult implements AssetDiscoveryInitiator {
		
		private final Map<String,Asset> assets = new HashMap<>();
		private final Map<String,Asset> assetsByPath = new HashMap<>();
		private final List<LinkedAsset> seedAssets = new ArrayList<>();
		private boolean furtherDiscoveryRequired = true;
		
		private AssetDiscoveryResult() {
			//TODO: do we need the loop?
			while (furtherDiscoveryRequired) {
				furtherDiscoveryRequired = false;
				discoverFurtherAssets(assetContainer.dir(), assetContainer.requirePrefix(), implicitDependencies);
			}
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
			String assetPrimaryRequirePath = asset.getPrimaryRequirePath();
			if (assets.containsKey(assetPrimaryRequirePath) ) {
				throw new RuntimeException("An asset for the require path '"+assetPrimaryRequirePath+"' has already been registered.");
			}
			assets.put(assetPrimaryRequirePath, asset);
			String assetPathRelativeToContainer = StringUtils.substringAfter(asset.getAssetPath(), assetContainer.root().dir().getRelativePath(assetContainer.dir()));
			if (assetPathRelativeToContainer.equals("")) {
				assetPathRelativeToContainer = ".";
			}
			assetsByPath.put(assetPathRelativeToContainer, asset);
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
			for (AssetPlugin assetPlugin : assetPlugins) {
				assetPlugin.discoverAssets(assetContainer, dir, requirePrefix, implicitDependencies, this);
			}
		}
		
	}
	
}
