package org.bladerunnerjs.plugin.plugins.bundlers.thirdparty;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.NonBladerunnerJsLibManifest;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.TestSourceModule;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class ThirdpartyAssetPlugin extends AbstractAssetPlugin {
	
	private final List<TestSourceModule> emptyTestSourceModules = new ArrayList<>();
	private final List<LinkedAsset> emptyLinkedAssets = new ArrayList<>();
	private final List<Asset> emptyAssets = new ArrayList<>();
	
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation)
	{
		try
		{
			List<SourceModule> sourceModules = new ArrayList<SourceModule>();
			if (assetLocation instanceof ThirdpartyAssetLocation)
			{
				NonBladerunnerJsLibManifest manifest = new NonBladerunnerJsLibManifest(assetLocation);
				ThirdpartySourceModule sourceModule = assetLocation.obtainAsset(ThirdpartySourceModule.class, assetLocation.dir(), "");
				sourceModule.initManifest(manifest);
				sourceModules.add( sourceModule );
			}
			return sourceModules;
		}
		catch (ConfigException | AssetFileInstantationException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public List<TestSourceModule> getTestSourceModules(AssetLocation assetLocation) {
		return emptyTestSourceModules;
	}
	
	@Override
	public List<LinkedAsset> getLinkedAssets(AssetLocation assetLocation) {
		return emptyLinkedAssets;
	}
	
	@Override
	public List<Asset> getAssets(AssetLocation assetLocation) {
		return emptyAssets;
	}
}
