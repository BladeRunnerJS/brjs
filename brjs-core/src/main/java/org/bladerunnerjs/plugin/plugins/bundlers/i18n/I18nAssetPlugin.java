package org.bladerunnerjs.plugin.plugins.bundlers.i18n;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;


public class I18nAssetPlugin extends AbstractAssetPlugin
{
	private Pattern i18nPropertiesFile = Pattern.compile(I18nAssetFile.I18N_PROPERTIES_FILE_REGEX);
	
	@Override
	public void setBRJS(BRJS brjs)
	{
	}
	
	@Override
	public List<Asset> getAssets(AssetLocation assetLocation)
	{
		return assetLocation._getAssets(this);
	}
	
	@Override
	public boolean canHandleAsset(File assetFile, AssetLocation assetLocation) {
		return i18nPropertiesFile.matcher(assetFile.getName()).matches();
	}
	
	@Override
	public Asset createAsset(File assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		return new I18nAssetFile(assetLocation, assetFile.getParentFile(), assetFile.getName());
	}
}
