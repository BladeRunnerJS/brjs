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
	public List<String> getAssetLocationDirectories(AssetContainer assetContainer) {
		initializePlugin();
		return assetLocationPlugin.getAssetLocationDirectories(assetContainer);
	}
	
	@Override
	public List<String> getSeedAssetLocationDirectories(AssetContainer assetContainer) {
		initializePlugin();
		return assetLocationPlugin.getSeedAssetLocationDirectories(assetContainer);
	}
	
	@Override
	public AssetLocation createAssetLocation(AssetContainer assetContainer, String dirPath, Map<String, AssetLocation> assetLocationsMap) {
		initializePlugin();
		return assetLocationPlugin.createAssetLocation(assetContainer, dirPath, assetLocationsMap);
	}
	
	@Override
	public boolean allowFurtherProcessing() {
		initializePlugin();
		return assetLocationPlugin.allowFurtherProcessing();
	}
}
