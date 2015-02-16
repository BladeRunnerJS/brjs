package org.bladerunnerjs.plugin.bundlers.html;

import java.io.FileFilter;
import java.util.List;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetDiscoveryInitiator;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.LinkedFileAsset;

public class HTMLAssetPlugin extends AbstractAssetPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}

	@Override
	public void discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		if (!requirePrefix.startsWith("html!")) {
			requirePrefix = "html!"+requirePrefix;
		}
		
		FileFilter htmlFileFilter = new SuffixFileFilter(".html");
		for (MemoizedFile htmlFile : dir.listFiles(htmlFileFilter)) {
			LinkedFileAsset asset = new LinkedFileAsset(htmlFile, assetContainer, requirePrefix);
			if (!assetDiscoveryInitiator.hasRegisteredAsset(asset.getPrimaryRequirePath())) {
				if (dir.isChildOf(assetContainer.file("resources"))) {
					assetDiscoveryInitiator.registerSeedAsset( asset );
				} else {
					assetDiscoveryInitiator.registerAsset( asset );
				}
			}
		}

	}
}
