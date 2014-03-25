package org.bladerunnerjs.plugin.proxy;

import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.plugin.AssetLocationPlugin;

public class VirtualProxyAssetLocationPlugin extends VirtualProxyPlugin implements AssetLocationPlugin {
	private AssetLocationPlugin assetLocationPlugin;
	
	public VirtualProxyAssetLocationPlugin(AssetLocationPlugin assetLocationPlugin) {
		super(assetLocationPlugin);
		this.assetLocationPlugin = assetLocationPlugin;
	}
	
	@Override
	public List<String> getPluginsThatMustAppearBeforeThisPlugin() {
		return assetLocationPlugin.getPluginsThatMustAppearBeforeThisPlugin();
	}
	
	@Override
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return assetLocationPlugin.getPluginsThatMustAppearAfterThisPlugin();
	}
	
	@Override
	public boolean canHandleAssetContainer(AssetContainer assetContainer) {
		initializePlugin();
		return assetLocationPlugin.canHandleAssetContainer(assetContainer);
	}
	
	@Override
	public List<AssetLocation> getAssetLocations(AssetContainer assetContainer, Map<String, AssetLocation> assetLocationCache) {
		initializePlugin();
		return assetLocationPlugin.getAssetLocations(assetContainer, assetLocationCache);
	}
}
