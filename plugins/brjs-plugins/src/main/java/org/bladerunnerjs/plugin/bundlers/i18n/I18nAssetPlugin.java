package org.bladerunnerjs.plugin.bundlers.i18n;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetFileInstantationException;


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
