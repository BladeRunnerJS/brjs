package org.bladerunnerjs.plugin.bundlers.commonjs;

import java.io.FileFilter;
import java.util.List;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetRegistry;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetContainer;


public class CommonJsAssetPlugin extends AbstractAssetPlugin
{
	private FileFilter jsFileFilter = new SuffixFileFilter(".js"); 
	
	@Override
	public void setBRJS(BRJS brjs)
	{
	}

	@Override
	public void discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetRegistry assetDiscoveryInitiator)
	{
		if (assetContainer.dir() == dir || !dir.jsStyle().equals(CommonJsSourceModule.JS_STYLE)) {
			return;
		}
		
		for (MemoizedFile jsFile : dir.listFiles(jsFileFilter)) {			
			if (!assetDiscoveryInitiator.hasRegisteredAsset(DefaultCommonJsSourceModule.calculateRequirePath(requirePrefix, jsFile))) {				
				if (jsFile.isChildOf(assetContainer.file("tests"))) {
					SourceModule commonJsModule = new TestCommonJsSourceModule(assetContainer, requirePrefix, jsFile, implicitDependencies);
					assetDiscoveryInitiator.registerSeedAsset( commonJsModule );
				} else {
					SourceModule commonJsModule = new DefaultCommonJsSourceModule(assetContainer, requirePrefix, jsFile, implicitDependencies);
					assetDiscoveryInitiator.registerAsset( commonJsModule );
				}
			}
		}
	}

}
