package org.bladerunnerjs.plugin.proxy;

import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetDiscoveryInitiator;
import org.bladerunnerjs.api.plugin.AssetPlugin;
import org.bladerunnerjs.model.AssetContainer;


public class VirtualProxyAssetLocationPlugin extends VirtualProxyPlugin implements AssetPlugin {
	
	private AssetPlugin assetLocationPlugin;

	public VirtualProxyAssetLocationPlugin(AssetPlugin assetLocationPlugin) {
		super(assetLocationPlugin);
		this.assetLocationPlugin = assetLocationPlugin;
	}

	@Override
	public void discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		initializePlugin();
		assetLocationPlugin.discoverAssets(assetContainer, dir, requirePrefix, implicitDependencies, assetDiscoveryInitiator);
	}
	
}
