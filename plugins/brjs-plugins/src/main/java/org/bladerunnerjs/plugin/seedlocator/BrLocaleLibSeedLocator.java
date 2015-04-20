package org.bladerunnerjs.plugin.seedlocator;

import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetRegistry;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetContainer;


public class BrLocaleLibSeedLocator extends AbstractAssetPlugin
{

	@Override
	public void discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetRegistry assetDiscoveryInitiator)
	{
		if (assetContainer instanceof JsLib) {
			JsLib jsLib = (JsLib) assetContainer;
			if (jsLib.getName().equals("br-locale")) {
				Asset brLocaleSeedAsset = assetDiscoveryInitiator.getRegisteredAsset("br-locale/switcher");
				if (brLocaleSeedAsset instanceof LinkedAsset && !assetDiscoveryInitiator.hasSeedAsset(brLocaleSeedAsset.getPrimaryRequirePath())) {
					assetDiscoveryInitiator.promoteRegisteredAssetToSeed( (LinkedAsset)brLocaleSeedAsset );
				}
			}
		}
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
		// TODO Auto-generated method stub
		
	}

}
