package org.bladerunnerjs.plugin.bundlers.thirdparty;

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.ThirdpartyLibManifest;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetRegistry;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.DirectoryAsset;
import org.bladerunnerjs.model.FileAsset;

public class ThirdpartyAssetPlugin extends AbstractAssetPlugin {
	
	@Override
	public void setBRJS(BRJS brjs) {
	}

	@Override
	public void discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetRegistry assetDiscoveryInitiator)
	{
		if ( assetContainer instanceof JsLib && assetContainer.file( ThirdpartyLibManifest.LIBRARY_MANIFEST_FILENAME ).exists()
				&& !assetDiscoveryInitiator.hasRegisteredAsset(ThirdpartySourceModule.calculateRequirePath(assetContainer)) ) {
			
			ThirdpartySourceModule asset = new ThirdpartySourceModule(assetContainer, implicitDependencies);
			assetDiscoveryInitiator.registerAsset(asset);
			asset.addImplicitDependencies( createDirectoryAssets(assetContainer, dir, assetContainer.requirePrefix(), assetDiscoveryInitiator) );

			// create CSS assets so they can be implicit dependencies based on the manifest file
			discoverCssAssets(assetContainer, dir, "css!"+assetContainer.requirePrefix(), assetDiscoveryInitiator);
		}
	}
	
	private List<Asset> discoverCssAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, AssetRegistry assetDiscoveryInitiator) {
		List<Asset> assets = new ArrayList<>();
		FileFilter cssFileFilter = new SuffixFileFilter(".css");
		for (MemoizedFile cssFile : dir.listFiles(cssFileFilter)) {
			Asset cssAsset;
			String assetRequirePath = FileAsset.calculateRequirePath(requirePrefix, cssFile);
			if ( !assetDiscoveryInitiator.hasRegisteredAsset(assetRequirePath) ) {
				cssAsset = new FileAsset(cssFile, assetContainer, requirePrefix);
				assetDiscoveryInitiator.registerAsset(cssAsset);
			} else {
				cssAsset = assetDiscoveryInitiator.getRegisteredAsset(assetRequirePath);
			}
			assets.add(cssAsset);
		}
		for (MemoizedFile childDir : dir.dirs()) {
			assets.addAll( discoverCssAssets(assetContainer, childDir, requirePrefix+"/"+childDir.getName(), assetDiscoveryInitiator) );
		}
		return assets;
	}
	
	private List<Asset> createDirectoryAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, AssetRegistry assetDiscoveryInitiator) {
		List<Asset> assets = new ArrayList<>();
		for (MemoizedFile assetDir : dir.dirs()) {
			Asset dirAsset;
			String assetDirRequirePath = DirectoryAsset.getRequirePath(requirePrefix, assetDir);
			if ( !assetDiscoveryInitiator.hasRegisteredAsset(assetDirRequirePath) ) {
				dirAsset = new DirectoryAsset(assetContainer, assetDir, requirePrefix, Arrays.asList());
				assetDiscoveryInitiator.registerAsset(dirAsset);
			} else {
				dirAsset = assetDiscoveryInitiator.getRegisteredAsset(assetDirRequirePath);
			}
			assets.add(dirAsset);
			assets.addAll( createDirectoryAssets(assetContainer, assetDir, requirePrefix+"/"+assetDir.getName(), assetDiscoveryInitiator) );
		}
		return assets;
	}
	
}