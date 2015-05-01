package org.bladerunnerjs.plugin.bundlers.html;

import java.io.FileFilter;
import java.util.List;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetRegistry;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.LinkedFileAsset;

public class HTMLAssetPlugin extends AbstractAssetPlugin {
	
	FileFilter htmlFileFilter = new SuffixFileFilter(".html");
	
	@Override
	public void setBRJS(BRJS brjs) {
	}

	@Override
	public void discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetRegistry assetDiscoveryInitiator)
	{
		if (assetContainer.dir() == dir) {
			return;
		}
		
		if (!requirePrefix.startsWith("html!")) {
			requirePrefix = "html!"+requirePrefix;
		}
		
		for (MemoizedFile htmlFile : dir.listFiles(htmlFileFilter)) {
			if (!assetDiscoveryInitiator.hasRegisteredAsset(LinkedFileAsset.calculateRequirePath(requirePrefix, htmlFile))) {
				LinkedFileAsset asset = new LinkedFileAsset(htmlFile, assetContainer, requirePrefix, implicitDependencies);
				if (dir.isChildOf(assetContainer.file("resources"))) {
					assetDiscoveryInitiator.registerSeedAsset( asset );
				} else {
					assetDiscoveryInitiator.registerAsset( asset );
				}
			}
		}
	}
}
