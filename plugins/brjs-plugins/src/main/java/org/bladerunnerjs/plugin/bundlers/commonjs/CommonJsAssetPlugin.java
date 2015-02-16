package org.bladerunnerjs.plugin.bundlers.commonjs;

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetDiscoveryInitiator;
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
	public List<Asset> discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		if (assetContainer.dir() == dir || !dir.jsStyle().equals(CommonJsSourceModule.JS_STYLE)) {
			return Collections.emptyList();
		}
		
		List<Asset> assets = new ArrayList<>();
		for (MemoizedFile jsFile : dir.listFiles(jsFileFilter)) {			
			boolean isTestFile = jsFile.isChildOf(assetContainer.file("tests"));
			SourceModule commonJsModule = (isTestFile) ? new TestCommonJsSourceModule(assetContainer, requirePrefix, jsFile) : new DefaultCommonJsSourceModule(assetContainer, requirePrefix, jsFile);
			assets.add(commonJsModule);
			if (!assetDiscoveryInitiator.hasRegisteredAsset(commonJsModule.getPrimaryRequirePath())) {				
				if (isTestFile) {
					assetDiscoveryInitiator.registerSeedAsset( commonJsModule );
				} else {
					assetDiscoveryInitiator.registerAsset( commonJsModule );					
				}
			}
		}
		return assets;
	}

}
