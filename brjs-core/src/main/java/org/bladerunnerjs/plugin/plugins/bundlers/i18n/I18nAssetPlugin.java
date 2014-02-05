package org.bladerunnerjs.plugin.plugins.bundlers.i18n;

import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetFilter;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.RegExAssetFilter;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;


public class I18nAssetPlugin extends AbstractAssetPlugin
{
	
	private AssetFilter i18nPropertiesFileFilter = new RegExAssetFilter(I18nAssetFile.I18N_PROPERTIES_FILE_REGEX);

	@Override
	public void setBRJS(BRJS brjs)
	{
	}

	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation)
	{
		return Arrays.asList();
	}

	@Override
	public List<LinkedAsset> getLinkedAssets(AssetLocation assetLocation)
	{
		return Arrays.asList();
	}

	@Override
	public List<Asset> getAssets(AssetLocation assetLocation)
	{
		try {
			return assetLocation.obtainMatchingAssets(i18nPropertiesFileFilter, Asset.class, I18nAssetFile.class);
		}
		catch (AssetFileInstantationException e) {
			throw new RuntimeException(e);
		}
	}

}
