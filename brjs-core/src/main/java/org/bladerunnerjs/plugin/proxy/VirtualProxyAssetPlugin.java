package org.bladerunnerjs.plugin.proxy;

import java.util.List;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.UnhandledAssetContainerException;
import org.bladerunnerjs.plugin.AssetPlugin;

public class VirtualProxyAssetPlugin extends VirtualProxyPlugin implements AssetPlugin {
	private AssetPlugin assetPlugin;
	
	@Override
	public List<AssetLocation> getAssetLocations(AssetContainer assetContainer) throws UnhandledAssetContainerException {
		initializePlugin();
		return assetPlugin.getAssetLocations(assetContainer);
	}
	
	public VirtualProxyAssetPlugin(AssetPlugin assetPlugin) {
		super(assetPlugin);
		this.assetPlugin = assetPlugin;
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation) {
		initializePlugin();
		return assetPlugin.getSourceModules(assetLocation);
	}
	
	@Override
	public List<LinkedAsset> getLinkedResourceFiles(AssetLocation assetLocation) {
		initializePlugin();
		return assetPlugin.getLinkedResourceFiles(assetLocation);
	}
	
	@Override
	public List<Asset> getResourceFiles(AssetLocation assetLocation) {
		initializePlugin();
		return assetPlugin.getResourceFiles(assetLocation);
	}
}
