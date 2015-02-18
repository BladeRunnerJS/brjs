package org.bladerunnerjs.plugin.plugins.bundlers.i18n;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;


public class I18nAssetPlugin extends AbstractAssetPlugin
{
	
	@Override
	public void setBRJS(BRJS brjs)
	{
	}
	
	@Override
	public boolean canHandleAsset(MemoizedFile assetFile, AssetLocation assetLocation) {
		return assetFile.getName().matches( Locale.LANGUAGE_AND_COUNTRY_CODE_FORMAT+"\\.properties" );
	}
	
	@Override
	public Asset createAsset(MemoizedFile assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		return new I18nFileAsset(assetFile, assetLocation);
	}
}
