package org.bladerunnerjs.plugin.proxy;

import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetDiscoveryInitiator;
import org.bladerunnerjs.api.plugin.AssetLocationPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetFileInstantationException;


public class VirtualProxyAssetLocationPlugin extends VirtualProxyPlugin implements AssetLocationPlugin {
	
	private AssetLocationPlugin assetLocationPlugin;

	public VirtualProxyAssetLocationPlugin(AssetLocationPlugin assetLocationPlugin) {
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
