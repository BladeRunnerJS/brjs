package org.bladerunnerjs.plugin.bundlers.thirdparty;

import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.ThirdpartyLibManifest;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetDiscoveryInitiator;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.FileAsset;

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
	public List<Asset> discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		if ((assetContainer instanceof JsLib) && (assetContainer.file( ThirdpartyLibManifest.LIBRARY_MANIFEST_FILENAME ).exists())) {
			if (assetDiscoveryInitiator.hasRegisteredAsset(ThirdpartySourceModule.calculateRequirePath(assetContainer))) {
				return Collections.emptyList();
			}
			
			ThirdpartySourceModule asset = new ThirdpartySourceModule(assetContainer);
			assetDiscoveryInitiator.registerAsset(asset);
			discoverCssAssets(assetContainer, dir, "css!"+assetContainer.requirePrefix(), assetDiscoveryInitiator);
			return Arrays.asList(asset);
		}
		return Collections.emptyList();
	}
	
	private void discoverCssAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, AssetDiscoveryInitiator assetDiscoveryInitiator) {
		FileFilter cssFileFilter = new SuffixFileFilter(".css");
		for (MemoizedFile cssFile : dir.listFiles(cssFileFilter)) {
			FileAsset cssAsset = new FileAsset(cssFile, assetContainer, requirePrefix);
			assetDiscoveryInitiator.registerAsset(cssAsset);
		}
		for (MemoizedFile childDir : dir.dirs()) {
			discoverCssAssets(assetContainer, childDir, requirePrefix+"/"+childDir.getName(), assetDiscoveryInitiator);
		}
	}
	
}