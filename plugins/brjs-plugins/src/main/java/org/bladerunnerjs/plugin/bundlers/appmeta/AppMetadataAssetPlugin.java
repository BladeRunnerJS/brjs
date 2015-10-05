package org.bladerunnerjs.plugin.bundlers.appmeta;

import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetRegistry;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetContainer;

public class AppMetadataAssetPlugin extends AbstractAssetPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}

	@Override
	public void discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetRegistry assetDiscoveryInitiator) {
		if((assetContainer instanceof BundlableNode) && !assetDiscoveryInitiator.hasRegisteredAsset(AppMetadataSourceModule.APP_META_DATA)) {
			assetDiscoveryInitiator.registerAsset(new AppMetadataSourceModule((BundlableNode) assetContainer));
		}
	}
}
