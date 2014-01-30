package org.bladerunnerjs.plugin.plugins.bundlers.i18n;

import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;


public class I18nAssetPlugin extends AbstractAssetPlugin
{
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		// TODO Auto-generated method stub
		
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
		return Arrays.asList();
	}

}
