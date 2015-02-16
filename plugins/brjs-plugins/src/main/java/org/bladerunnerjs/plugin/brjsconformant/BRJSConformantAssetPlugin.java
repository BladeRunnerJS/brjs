package org.bladerunnerjs.plugin.brjsconformant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	public List<Asset> discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		if (assetContainer instanceof DefaultBladeset || !neccessaryChildDirsArePresent(assetContainer)) {
			return Collections.emptyList();
		}
		
		List<Asset> assets = new ArrayList<>();
		if (assetContainer.dir() == dir) {
			assets.addAll( createAssetsForRootDirs(assetContainer, dir, requirePrefix, implicitDependencies, assetDiscoveryInitiator) );
		} else if (dir == assetContainer.file("src") || dir == assetContainer.file("src-test") || dir == assetContainer.file("tests") || (assetContainer instanceof TestPack && assetContainer.dir() == dir)) {
			assets.addAll( createAssetsForSrcDirs(assetContainer, dir, requirePrefix, implicitDependencies, assetDiscoveryInitiator) );
		} else if (dir == assetContainer.file("themes")) {
			assets.addAll( createAssetsForThemeDirs(assetContainer, dir, requirePrefix, implicitDependencies, assetDiscoveryInitiator) );
		} else {
			assets.addAll( createAssetsForChildDirs(assetContainer, dir, requirePrefix, implicitDependencies, assetDiscoveryInitiator) );
		}
		
		return assets;
	}

	private List<Asset> createAssetsForChildDirs(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		List<Asset> assets = new ArrayList<>();
		for (MemoizedFile childDir : dir.dirs()) {
			LinkedAsset child = new DirectoryLinkedAsset(assetContainer, childDir, requirePrefix);
			assets.add(child);
			if (!assetDiscoveryInitiator.hasRegisteredAsset(child.getPrimaryRequirePath())) {
				assetDiscoveryInitiator.registerAsset(child);				
				assetDiscoveryInitiator.discoverFurtherAssets(childDir, child.getPrimaryRequirePath(), implicitDependencies);
			}
		}
		return assets;
	}

	private List<Asset> createAssetsForSrcDirs(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		String rootRequirePrefix = StringUtils.substringBefore(assetContainer.requirePrefix(), "/");
		MemoizedFile srcPackageRoot = dir;
		if (dir.file(rootRequirePrefix).isDirectory()) {
			srcPackageRoot = dir.file(assetContainer.requirePrefix());
			assetDiscoveryInitiator.discoverFurtherAssets(srcPackageRoot, requirePrefix, implicitDependencies);
		}
		createAssetsForChildDirs(assetContainer, srcPackageRoot, requirePrefix, implicitDependencies, assetDiscoveryInitiator);
		return Collections.emptyList();
	}
	
	private List<Asset> createAssetsForThemeDirs(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		for (MemoizedFile themeDir : dir.dirs()) {
			String themeRequirePath = "theme!"+themeDir.getName()+":";
			assetDiscoveryInitiator.discoverFurtherAssets(themeDir, themeRequirePath, implicitDependencies);			
		}
		return Collections.emptyList();
	}

	private List<Asset> createAssetsForRootDirs(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		if (assetDiscoveryInitiator.hasRegisteredAsset(requirePrefix)) {
			return Collections.emptyList();
		}
		LinkedAsset rootAsset = new BRJSConformantRootDirectoryLinkedAsset(assetContainer); 
		assetDiscoveryInitiator.registerAsset(rootAsset);
		
		for (MemoizedFile childDir : getPossibleChildDirs(assetContainer)) {
			assetDiscoveryInitiator.discoverFurtherAssets(childDir, requirePrefix, implicitDependencies);
		}
		
		return Arrays.asList(rootAsset);
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
