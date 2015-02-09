package org.bladerunnerjs.plugin.bundlers.thirdparty;

import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.ThirdpartyLibManifest;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetDiscoveryInitiator;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetContainer;

public class ThirdpartyAssetPlugin extends AbstractAssetPlugin {
	
	@Override
	public int priority()
	{
		return 0;
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
	}

	@Override
	public void discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		if ((assetContainer instanceof JsLib) && (assetContainer.file( ThirdpartyLibManifest.LIBRARY_MANIFEST_FILENAME ).exists())) {
			Asset asset = new ThirdpartySourceModule(assetContainer);
			if (!assetDiscoveryInitiator.hasRegisteredAsset(asset.getPrimaryRequirePath())) {
				assetDiscoveryInitiator.registerAsset(asset);
			}
		}
	}
	
}