package org.bladerunnerjs.plugin.bundlers.commonjs;

import java.io.FileFilter;
import java.util.List;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetDiscoveryInitiator;
import org.bladerunnerjs.api.plugin.base.AbstractAssetLocationPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetFileInstantationException;


public class CommonJsAssetLocationPlugin extends AbstractAssetLocationPlugin
{
	
	@Override
	public void setBRJS(BRJS brjs)
	{
	}

	@Override
	public void discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		if (assetContainer.dir() == dir || !dir.jsStyle().equals(CommonJsSourceModule.JS_STYLE)) {
			return;
		}
		
		FileFilter jsFileFilter = new SuffixFileFilter(".js");
		for (MemoizedFile jsFile : dir.listFiles(jsFileFilter)) {
			assetDiscoveryInitiator.registerAsset( new DefaultCommonJsSourceModule(assetContainer, requirePrefix, jsFile) );
		}
	}

}
