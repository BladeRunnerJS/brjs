package org.bladerunnerjs.plugin.bundlers.css;

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
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
	
	private FileFilter cssFileFilter = new SuffixFileFilter(".css");
	
	@Override
	public void setBRJS(BRJS brjs) {
	}

	@Override
	public List<Asset> discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		if (assetContainer.dir() == dir) {
			return Collections.emptyList();
		}
		
		if (!requirePrefix.contains("!")) {
			requirePrefix = "css!"+requirePrefix;
		}
		
		List<Asset> assets = new ArrayList<>();
		for (MemoizedFile cssFile : dir.listFiles(cssFileFilter)) {
			
			if (!assetDiscoveryInitiator.hasRegisteredAsset(FileAsset.calculateRequirePath(requirePrefix, cssFile))) {
				Asset asset = new FileAsset(cssFile, assetContainer, requirePrefix);
				assets.add(asset);
				assetDiscoveryInitiator.registerAsset( asset );
			}
		}
		return assets;
	}
}
