package org.bladerunnerjs.plugin.bundlers.css;

import java.io.FileFilter;
import java.util.List;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetDiscoveryInitiator;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.FileAsset;

public class CssAssetPlugin extends AbstractAssetPlugin {
	
	@Override
	public void setBRJS(BRJS brjs) {
	}

	@Override
	public void discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		if (!requirePrefix.startsWith("css!")) {
			requirePrefix = "css!"+requirePrefix;
		}
		
		FileFilter cssFileFilter = new SuffixFileFilter(".css");
		for (MemoizedFile cssFile : dir.listFiles(cssFileFilter)) {
			Asset asset = new FileAsset(cssFile, assetContainer, requirePrefix);
			if (!assetDiscoveryInitiator.hasRegisteredAsset(asset.getPrimaryRequirePath())) {
				assetDiscoveryInitiator.registerAsset( asset );
			}
		}
	}
}
