package org.bladerunnerjs.api.plugin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.model.AssetContainer;


public class AssetContainerAssets
{
	private final MemoizedValue<DefaultAssetRegistry> assetDiscoveryResult;

	private AssetContainer assetContainer;
	
	public AssetContainerAssets(AssetContainer assetContainer)
	{
		this.assetContainer = assetContainer;
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
	
	private DefaultAssetRegistry assetDiscoveryResult() {
		return assetDiscoveryResult.value(() -> {
			return new DefaultAssetRegistry(assetContainer);
		});
	}
	
}
