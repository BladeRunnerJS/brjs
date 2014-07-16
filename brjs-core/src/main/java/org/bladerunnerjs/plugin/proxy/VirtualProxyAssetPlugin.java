package org.bladerunnerjs.plugin.proxy;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.plugin.AssetPlugin;

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
	public boolean canHandleAsset(File assetFile, AssetLocation assetLocation) {
		initializePlugin();
		return assetPlugin.canHandleAsset(assetFile, assetLocation);
	}
	
	@Override
	public Asset createAsset(File assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		initializePlugin();
		return assetPlugin.createAsset(assetFile, assetLocation);
	}
}
