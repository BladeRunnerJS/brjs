package org.bladerunnerjs.plugin.bundlers.html;

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
import org.bladerunnerjs.model.LinkedFileAsset;

public class HTMLAssetPlugin extends AbstractAssetPlugin {
	
	FileFilter htmlFileFilter = new SuffixFileFilter(".html");
	
	@Override
	public void setBRJS(BRJS brjs) {
	}

	@Override
	public List<Asset> discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		if (assetContainer.dir() == dir) {
			return Collections.emptyList();
		}
		
		if (!requirePrefix.startsWith("html!")) {
			requirePrefix = "html!"+requirePrefix;
		}
		
		List<Asset> assets = new ArrayList<>();
		for (MemoizedFile htmlFile : dir.listFiles(htmlFileFilter)) {
			if (!assetDiscoveryInitiator.hasRegisteredAsset(LinkedFileAsset.calculateRequirePath(requirePrefix, htmlFile))) {
				LinkedFileAsset asset = new LinkedFileAsset(htmlFile, assetContainer, requirePrefix, implicitDependencies);
				assets.add(asset);
				if (dir.isChildOf(assetContainer.file("resources"))) {
					assetDiscoveryInitiator.registerSeedAsset( asset );
				} else {
					assetDiscoveryInitiator.registerAsset( asset );
				}
			}
		}
		return assets;
	}
}
