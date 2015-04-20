package org.bladerunnerjs.plugin.bundlers.i18n;

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetRegistry;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetContainer;


public class I18nAssetPlugin extends AbstractAssetPlugin
{
	
	FileFilter i18nFileFilter = new RegexFileFilter(Locale.LANGUAGE_AND_COUNTRY_CODE_FORMAT+"\\.properties");
	
	@Override
	public void setBRJS(BRJS brjs)
	{
	}

	@Override
	public List<Asset> discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetRegistry assetDiscoveryInitiator)
	{
		if (assetContainer.dir() == dir) {
			return Collections.emptyList();
		}
		
		if (!requirePrefix.startsWith("i18n!")) {
			requirePrefix = "i18n!"+requirePrefix;
		}
		
		List<Asset> assets = new ArrayList<>();
		for (MemoizedFile i18nFile : dir.listFiles(i18nFileFilter)) {
			if (!assetDiscoveryInitiator.hasRegisteredAsset(I18nFileAsset.calculateRequirePath(requirePrefix, i18nFile))) {
				Asset asset = new I18nFileAsset(i18nFile, assetContainer, requirePrefix);
				assets.add(asset);
				assetDiscoveryInitiator.registerAsset( asset );
			}
		}
		return assets;
	}
}
