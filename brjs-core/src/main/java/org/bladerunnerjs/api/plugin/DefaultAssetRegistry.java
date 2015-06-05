package org.bladerunnerjs.api.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetContainer;

public class DefaultAssetRegistry implements AssetRegistry {

	final Map<String,Asset> assets = new LinkedHashMap<>();
	final List<LinkedAsset> seedAssets = new ArrayList<>();
	private AssetContainer assetContainer;
	private Stack<List<Asset>> currentDirectoryAssetList = new Stack<>();
	
	public DefaultAssetRegistry(AssetContainer assetContainer) {
		this.assetContainer = assetContainer;
		discoverFurtherAssets(assetContainer.dir(), assetContainer.requirePrefix(), Collections.emptyList());
	}
	
	@Override
	public void registerSeedAsset(LinkedAsset asset)
	{
		registerAsset(asset);
		seedAssets.add(asset);
	}
	
	@Override
	public void promoteRegisteredAssetToSeed(LinkedAsset asset)
	{
		if (!assets.containsKey(asset.getPrimaryRequirePath())) {
			throw new RuntimeException(
					String.format("No asset with the require path '%s' has been previously registered.", asset.getPrimaryRequirePath())
			); 
		}
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
		currentDirectoryAssetList.peek().add(asset);
	}

	@Override
	public boolean hasSeedAsset(String requirePath)
	{
		if (!hasRegisteredAsset(requirePath)) {
			return false;
		}
		return seedAssets.contains( getRegisteredAsset(requirePath) );
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
	public Set<Asset> getRegisteredAssets()
	{
		return new LinkedHashSet<>( assets.values() );
	}

	@Override
	public Set<LinkedAsset> getRegisteredSeedAssets()
	{
		return new LinkedHashSet<>( seedAssets );
	}
	
	@Override
	public List<Asset> discoverFurtherAssets(MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies)
	{
		currentDirectoryAssetList.push(new ArrayList<>());
		for (AssetPlugin assetPlugin : assetContainer.root().plugins().assetPlugins()) {
			// do not remove the new ArrayList - we need to pass a new instance so SourceModules aren't all changing the same instance
			assetPlugin.discoverAssets(assetContainer, dir, requirePrefix, new ArrayList<>(implicitDependencies), this);
		}
		return discoveredAssets();
	}
	
	private List<Asset> discoveredAssets() {
		return currentDirectoryAssetList.pop();
	}
	
}