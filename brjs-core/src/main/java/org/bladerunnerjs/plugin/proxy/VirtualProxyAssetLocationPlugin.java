package org.bladerunnerjs.plugin.proxy;

import java.util.List;

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
	public List<AssetLocation> getAssetLocations(AssetContainer assetContainer) {
		initializePlugin();
		return assetLocationPlugin.getAssetLocations(assetContainer);
	}
}
