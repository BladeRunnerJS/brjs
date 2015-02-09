package org.bladerunnerjs.plugin.proxy;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.LegacyAssetPlugin;
import org.bladerunnerjs.model.AssetFileInstantationException;

public class LegacyVirtualProxyAssetPlugin extends VirtualProxyPlugin implements LegacyAssetPlugin {
	private LegacyAssetPlugin assetPlugin;
	
	public LegacyVirtualProxyAssetPlugin(LegacyAssetPlugin assetPlugin) {
		super(assetPlugin);
		this.assetPlugin = assetPlugin;
	}
	
	@Override
	public int priority() {
		return assetPlugin.priority();
	}
	
	@Override
	public boolean canHandleAsset(MemoizedFile assetFile, AssetLocation assetLocation) {
		initializePlugin();
		return assetPlugin.canHandleAsset(assetFile, assetLocation);
	}
	
	@Override
	public Asset createAsset(MemoizedFile assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		initializePlugin();
		return assetPlugin.createAsset(assetFile, assetLocation);
	}
}
