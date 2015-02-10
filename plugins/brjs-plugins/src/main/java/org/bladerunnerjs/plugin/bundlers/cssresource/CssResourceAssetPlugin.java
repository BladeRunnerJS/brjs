package org.bladerunnerjs.plugin.bundlers.cssresource;

import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetDiscoveryInitiator;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.FileAsset;

public class CssResourceAssetPlugin extends AbstractAssetPlugin {
	
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}

	@Override
	public void discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		FileFilter assetFileFilter = new SuffixFileFilter( Arrays.asList(".jpg",".jpeg",".bmp",".png",".gif",".svg",".ico",".cur",".eot",".ttf",".woff") );
		for (MemoizedFile assetFile : dir.listFiles(assetFileFilter)) {
			String assetExtension = StringUtils.substringAfterLast(assetFile.getName(), ".");
			Asset asset = new FileAsset(assetFile, assetContainer, assetExtension+"!"+requirePrefix);
			if (!assetDiscoveryInitiator.hasRegisteredAsset(asset.getPrimaryRequirePath())) {
				assetDiscoveryInitiator.registerAsset( asset );
			}
		}

	}
	
}
