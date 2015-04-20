package org.bladerunnerjs.plugin.bundlers.namespacedjs;

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetRegistry;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetContainer;

public class NamespacedJsAssetPlugin extends AbstractAssetPlugin {
	
	FileFilter jsFileFilter = new SuffixFileFilter(".js");
	
	@Override
	public void setBRJS(BRJS brjs) {
	}

	@Override
	public List<Asset> discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetRegistry assetDiscoveryInitiator)
	{
		if (assetContainer.dir() == dir || !dir.jsStyle().equals(NamespacedJsSourceModule.JS_STYLE)) {
			return Collections.emptyList();
		}
		
		List<Asset> assets = new ArrayList<>();
		for (MemoizedFile jsFile : dir.listFiles(jsFileFilter)) {
			if (!assetDiscoveryInitiator.hasRegisteredAsset(NamespacedJsSourceModule.calculateRequirePath(requirePrefix, jsFile))) {
				if (jsFile.isChildOf(assetContainer.file("tests"))) {
					SourceModule sourceModule = new TestNamespacedJsSourceModule(assetContainer, requirePrefix, jsFile, implicitDependencies);
					assets.add(sourceModule);
					assetDiscoveryInitiator.registerSeedAsset( sourceModule );
				} else {
					SourceModule sourceModule = new NamespacedJsSourceModule(assetContainer, requirePrefix, jsFile, implicitDependencies);
					assets.add(sourceModule);
					assetDiscoveryInitiator.registerAsset( sourceModule );										
				}
				
			}
		}
		return assets;
	}
}
