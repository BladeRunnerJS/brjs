package org.bladerunnerjs.plugin.brjsconformant;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
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
			String relativePathFromAssetContainer = assetContainer.dir().getRelativePath(childDir);
			if (relativePathFromAssetContainer.startsWith("themes") && relativePathFromAssetContainer.split("/").length < 3) {
					assetDiscoveryInitiator.discoverFurtherAssets(childDir, requirePrefix, implicitDependencies);
					return;			
			}
			String childDirRequirePath = calculateChildDirRequirePath(relativePathFromAssetContainer);
			LinkedAsset child = new DirectoryLinkedAsset(assetContainer, childDir, childDirRequirePath);
			if (!assetDiscoveryInitiator.hasRegisteredAsset(childDirRequirePath)) {
				assetDiscoveryInitiator.registerAsset(child);				
				assetDiscoveryInitiator.discoverFurtherAssets(childDir, child.getPrimaryRequirePath(), implicitDependencies);
			}
		}
	}

	private String calculateChildDirRequirePath(String relativePathFromAssetContainer)
	{
		if (relativePathFromAssetContainer.startsWith("themes")) {
			String[] relativePathFromAssetContainerSplit = relativePathFromAssetContainer.split("/");
    		String themeName = relativePathFromAssetContainerSplit[0];
    		String themeDirRequirePrefix = StringUtils.join( ArrayUtils.subarray(relativePathFromAssetContainerSplit, 2, relativePathFromAssetContainerSplit.length), "/");
    		return "theme!"+themeName+":"+themeDirRequirePrefix;
        } else {
        	return StringUtils.substringAfter(relativePathFromAssetContainer, "/");
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
