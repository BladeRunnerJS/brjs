package org.bladerunnerjs.plugin.proxy;

import java.util.List;
import java.io.File;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.plugin.AssetPlugin;

public class VirtualProxyAssetPlugin extends VirtualProxyPlugin implements AssetPlugin {
	private AssetPlugin assetPlugin;
	
	@Override
	public List<AssetLocation> getAssetLocations(AssetContainer assetContainer) {
		initializePlugin();
		return assetPlugin.getAssetLocations(assetContainer);
	}
	
	public VirtualProxyAssetPlugin(AssetPlugin assetPlugin) {
		super(assetPlugin);
		this.assetPlugin = assetPlugin;
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation, List<File> files) {
		initializePlugin();
		return assetPlugin.getSourceModules(assetLocation, files);
	}
	
	@Override
	public List<LinkedAsset> getLinkedResourceFiles(AssetLocation assetLocation, List<File> files) {
		initializePlugin();
		return assetPlugin.getLinkedResourceFiles(assetLocation, files);
	}
	
	@Override
	public List<Asset> getResourceFiles(AssetLocation assetLocation, List<File> files) {
		initializePlugin();
		return assetPlugin.getResourceFiles(assetLocation, files);
	}
}
