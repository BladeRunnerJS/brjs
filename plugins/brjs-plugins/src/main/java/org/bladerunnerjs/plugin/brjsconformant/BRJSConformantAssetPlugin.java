package org.bladerunnerjs.plugin.brjsconformant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.JsLib;
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
		if (assetContainer instanceof DefaultBladeset || !neccessaryChildDirsArePresent(assetContainer)) {
			return;
		}
		
		if (assetContainer.dir() == dir) {
			createAssetsForRootDirs(assetContainer, dir, requirePrefix, implicitDependencies, assetDiscoveryInitiator);
		} else if (assetContainer.file("src") == dir) {
			createAssetsForSrcDirs(assetContainer, dir, requirePrefix, implicitDependencies, assetDiscoveryInitiator);
		} else if (assetContainer.file("themes") == dir) {
			createAssetsForThemeDirs(assetContainer, dir, requirePrefix, implicitDependencies, assetDiscoveryInitiator);
		} else {
			createAssetsForChildDirs(assetContainer, dir, requirePrefix, implicitDependencies, assetDiscoveryInitiator);
		}
	}

	private void createAssetsForChildDirs(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		for (MemoizedFile childDir : dir.dirs()) {
			LinkedAsset child = new DirectoryLinkedAsset(assetContainer, childDir, requirePrefix);
			if (!assetDiscoveryInitiator.hasRegisteredAsset(child.getPrimaryRequirePath())) {
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
	
	private void createAssetsForThemeDirs(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		for (MemoizedFile themeDir : dir.dirs()) {
			String themeRequirePath = "theme!"+themeDir.getName()+":";
			assetDiscoveryInitiator.discoverFurtherAssets(themeDir, themeRequirePath, implicitDependencies);			
		}
	}

	private void createAssetsForRootDirs(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		if (assetDiscoveryInitiator.hasRegisteredAsset(requirePrefix)) {
			return;
		}
		LinkedAsset rootAsset = new BRJSConformantRootDirectoryLinkedAsset(assetContainer); 
		assetDiscoveryInitiator.registerAsset(rootAsset);
		
		for (MemoizedFile childDir : getPossibleChildDirs(assetContainer)) {
			assetDiscoveryInitiator.discoverFurtherAssets(childDir, requirePrefix, implicitDependencies);
		}
	}
	
	private List<MemoizedFile> getPossibleChildDirs(AssetContainer assetContainer) {
		List<String> childPaths;
		List<MemoizedFile> childDirs = new ArrayList<>();
		if (assetContainer instanceof TestPack) {
			childPaths = Arrays.asList("tests", "src-test", "resources");
		} else {
			childPaths = Arrays.asList("src", "src-test", "themes", "resources");
		}
		
		for (String childPath : childPaths) {
			childDirs.add( assetContainer.dir().file(childPath) );
		}
		
		return childDirs;
	}
	
	private boolean neccessaryChildDirsArePresent(AssetContainer assetContainer) {
		for (MemoizedFile childDir : getPossibleChildDirs(assetContainer)) {
			if (childDir.isDirectory()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
	}
	
}
