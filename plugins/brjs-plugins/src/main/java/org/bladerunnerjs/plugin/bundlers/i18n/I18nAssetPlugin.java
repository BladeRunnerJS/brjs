package org.bladerunnerjs.plugin.bundlers.i18n;

import java.io.FileFilter;
import java.util.List;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetDiscoveryInitiator;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetContainer;


public class I18nAssetPlugin extends AbstractAssetPlugin
{
	
	@Override
	public void setBRJS(BRJS brjs)
	{
	}

	@Override
	public void discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		if (!requirePrefix.startsWith("i18n!")) {
			requirePrefix = "i18n!"+requirePrefix;
		}
		
		FileFilter i18nFileFilter = new RegexFileFilter(Locale.LANGUAGE_AND_COUNTRY_CODE_FORMAT+"\\.properties");
		
		for (MemoizedFile i18nFile : dir.listFiles(i18nFileFilter)) {
			Asset asset = new I18nFileAsset(i18nFile, assetContainer, requirePrefix);
			if (!assetDiscoveryInitiator.hasRegisteredAsset(asset.getPrimaryRequirePath())) {
				assetDiscoveryInitiator.registerAsset( asset );
			}
		}
	}
}
