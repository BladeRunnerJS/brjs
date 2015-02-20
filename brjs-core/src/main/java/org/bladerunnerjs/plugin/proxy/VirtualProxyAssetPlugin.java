package org.bladerunnerjs.plugin.proxy;

import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetPlugin;
import org.bladerunnerjs.model.AssetFileInstantationException;

public class VirtualProxyAssetPlugin extends VirtualProxyPlugin implements AssetPlugin {
	private AssetPlugin assetPlugin;
	
	public VirtualProxyAssetPlugin(AssetPlugin assetPlugin) {
		super(assetPlugin);
		this.assetPlugin = assetPlugin;
	}
	
	public List<String> getPluginsThatMustAppearBeforeThisPlugin() {
		return assetPlugin.getPluginsThatMustAppearBeforeThisPlugin();
	}
	
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return assetPlugin.getPluginsThatMustAppearAfterThisPlugin();
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
