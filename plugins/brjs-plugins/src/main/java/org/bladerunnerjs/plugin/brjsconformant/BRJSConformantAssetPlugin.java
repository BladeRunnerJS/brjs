package org.bladerunnerjs.plugin.brjsconformant;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetDiscoveryInitiator;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.DefaultBladeset;
import org.bladerunnerjs.model.DirectoryLinkedAsset;


public class BRJSConformantAssetPlugin extends AbstractAssetPlugin
{

	@Override
	public int priority()
	{
		return 100;
	}
	
	@Override
	public void discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		if (assetContainer instanceof DefaultBladeset) {
			return;
		}
		
		if (assetContainer.dir() == dir) {
			createAssetsForRootDirs(assetContainer, dir, requirePrefix, implicitDependencies, assetDiscoveryInitiator);
		} else {
			if (assetContainer.file("src") == dir) {
				createAssetsForSrcDirs(assetContainer, dir, requirePrefix, implicitDependencies, assetDiscoveryInitiator);
			} else {
    			createAssetsForChildDirs(assetContainer, dir, requirePrefix, implicitDependencies, assetDiscoveryInitiator);
			}
		}
	}

	private void createAssetsForChildDirs(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		for (MemoizedFile childDir : dir.dirs()) {
			String childDirRequirePath = StringUtils.substringAfter(assetContainer.dir().getRelativePath(childDir), "/"); // strip of the preceding 'src' or 'src-test' etc
			LinkedAsset child = new DirectoryLinkedAsset(assetContainer, childDir, childDirRequirePath);
			if (!assetDiscoveryInitiator.hasRegisteredAsset(childDirRequirePath)) {
    			assetDiscoveryInitiator.registerAsset(child);				
    			assetDiscoveryInitiator.discoverFurtherAssets(childDir, child.getPrimaryRequirePath(), implicitDependencies);
			}
		}
	}

	private void createAssetsForSrcDirs(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		String rootRequirePrefix = StringUtils.substringBefore(assetContainer.requirePrefix(), "/");
		MemoizedFile srcPackageRoot = dir;
		if (dir.file(rootRequirePrefix).isDirectory()) {
			srcPackageRoot = dir.file(assetContainer.requirePrefix());
			assetDiscoveryInitiator.discoverFurtherAssets(srcPackageRoot, requirePrefix, implicitDependencies);
		}
		createAssetsForChildDirs(assetContainer, srcPackageRoot, requirePrefix, implicitDependencies, assetDiscoveryInitiator);
	}

	private void createAssetsForRootDirs(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		if (assetDiscoveryInitiator.hasRegisteredAsset(requirePrefix)) {
			return;
		}
		LinkedAsset rootAsset = new DirectoryLinkedAsset(assetContainer, assetContainer.dir(), requirePrefix); 
		assetDiscoveryInitiator.registerAsset(rootAsset);
		List<String> childPaths;
		
		if (assetContainer instanceof TestPack) {
			childPaths = Arrays.asList("tests", "src-test", "themes", "resources");
		} else {
			childPaths = Arrays.asList("src", "src-test", "themes", "resources");
		}
		
		for (String childPath : childPaths) {
			MemoizedFile childDir = dir.file(childPath);
			assetDiscoveryInitiator.discoverFurtherAssets(childDir, requirePrefix, implicitDependencies);
		}
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
	}	
	
}
