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
		// only create assets if we're at the root of the asset container *and* its not a default bladeset
		if (assetContainer instanceof DefaultBladeset || !neccessaryChildDirsArePresent(assetContainer) || assetContainer.dir() != dir) {
			return Collections.emptyList();
		}
		
		List<Asset> assets = new ArrayList<>();
		
		LinkedAsset rootAsset = new BRJSConformantRootDirectoryLinkedAsset(assetContainer); 
		assetDiscoveryInitiator.registerAsset(rootAsset);
		assets.add(rootAsset);
		
		for (MemoizedFile srcDir : getSrcDirs(assetContainer)) {
			discoverFurtherAssetsForChild(assetContainer, srcDir, requirePrefix, implicitDependencies, assetDiscoveryInitiator, rootAsset);
		}
		
		List<Asset> implicitResourcesDependencies = new ArrayList<>();
		implicitResourcesDependencies.addAll(implicitDependencies);
		implicitResourcesDependencies.add(rootAsset);
		for (MemoizedFile resourceDir : getResourceDirs(assetContainer)) {
			createAssetsForChildDir(assetContainer, resourceDir, requirePrefix, implicitResourcesDependencies, assetDiscoveryInitiator, rootAsset);
		}
		
		for (MemoizedFile testDir : getTestDirs(assetContainer)) {
			discoverFurtherAssetsForChild(assetContainer, testDir, requirePrefix, implicitDependencies, assetDiscoveryInitiator, rootAsset);
		}
		
		for (MemoizedFile themeDir : getThemeDirs(assetContainer)) {
			String themeRequirePrefix = "theme!"+themeDir.getName()+":"+requirePrefix;
			createAssetsForChildDir(assetContainer, themeDir, themeRequirePrefix, implicitDependencies, assetDiscoveryInitiator, rootAsset);
		}
		
		return Arrays.asList(rootAsset);
	}

	private List<MemoizedFile> getSrcDirs(AssetContainer assetContainer)
	{
		String rootRequirePrefix = StringUtils.substringBefore(assetContainer.requirePrefix(), "/");
		if (assetContainer instanceof TestPack) {
			String srcTestDir = (assetContainer.file("src-test/"+rootRequirePrefix).isDirectory()) ? "src-test/"+assetContainer.requirePrefix() : "src-test";
			return createFilesForFilePaths(assetContainer,  Arrays.asList(srcTestDir) );
		} else {
			String srcDir = (assetContainer.file("src/"+rootRequirePrefix).isDirectory()) ? "src/"+assetContainer.requirePrefix() : "src";
			String srcTestDir = (assetContainer.file("src-test/"+rootRequirePrefix).isDirectory()) ? "src-test/"+assetContainer.requirePrefix() : "src-test";
			return createFilesForFilePaths(assetContainer, Arrays.asList(srcDir, srcTestDir) );
		}
	}
	
	private List<MemoizedFile> getThemeDirs(AssetContainer assetContainer)
	{
		if (assetContainer instanceof TestPack) {
			return Collections.emptyList();
		}
		List<MemoizedFile> themeDirs = new ArrayList<>();
		for (MemoizedFile themeDir : assetContainer.file("themes").dirs()) {
			themeDirs.add(themeDir);
		}
		return themeDirs;
	}
	
	private List<MemoizedFile> getResourceDirs(AssetContainer assetContainer)
	{
		if (assetContainer instanceof TestPack) {
			return Collections.emptyList();
		} else {
			return createFilesForFilePaths(assetContainer, Arrays.asList("resources") );
		}
	}
	
	private List<MemoizedFile> getTestDirs(AssetContainer assetContainer)
	{
		if (assetContainer instanceof TestPack) {
			return createFilesForFilePaths(assetContainer, Arrays.asList("tests") );
		} else {
			return Collections.emptyList();
		}
	}

	private void createAssetsForChildDir(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, 
			AssetDiscoveryInitiator assetDiscoveryInitiator, Asset parentAsset)
	{
		Asset child = getOrCreateAsset(assetContainer, dir, requirePrefix, assetDiscoveryInitiator);
		discoverFurtherAssetsForChild(assetContainer, dir, child.getPrimaryRequirePath(), implicitDependencies, assetDiscoveryInitiator, child);
	}
	
	private void discoverFurtherAssetsForChild(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator, Asset parent)
	{
		List<Asset> furtherAssetImplicitDependencies = new ArrayList<>();
		furtherAssetImplicitDependencies.addAll(implicitDependencies);
		furtherAssetImplicitDependencies.add(parent);
		
		List<Asset> discoveredAssets = assetDiscoveryInitiator.discoverFurtherAssets(dir, requirePrefix, furtherAssetImplicitDependencies);
		if (parent instanceof LinkedAsset) {
			((LinkedAsset) parent).addImplicitDependencies(discoveredAssets);
		}
		
		for (MemoizedFile childDir : dir.dirs()) {
			createAssetsForChildDir(assetContainer, childDir, requirePrefix, implicitDependencies, assetDiscoveryInitiator, parent );
		}
	}

	public Asset getOrCreateAsset(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, AssetDiscoveryInitiator assetDiscoveryInitiator) {
		if (!assetDiscoveryInitiator.hasRegisteredAsset(DirectoryLinkedAsset.getRequirePath(requirePrefix, dir))) {
			Asset asset = new DirectoryLinkedAsset(assetContainer, dir, requirePrefix);
			assetDiscoveryInitiator.registerAsset(asset);
			return asset;
		} else {
			return assetDiscoveryInitiator.getRegisteredAsset( DirectoryLinkedAsset.getRequirePath(requirePrefix, dir) );
		}
	}
	
	private List<MemoizedFile> createFilesForFilePaths(AssetContainer assetContainer, List<String> filePaths) {
		List<MemoizedFile> files = new ArrayList<>();
		for (String filePath : filePaths) {
			files.add( assetContainer.file(filePath) );
		}
		return files;
	}

	private boolean neccessaryChildDirsArePresent(AssetContainer assetContainer) {
		List<MemoizedFile> expectedDirs = getSrcDirs(assetContainer);
		expectedDirs.addAll(getResourceDirs(assetContainer));
		expectedDirs.addAll(getTestDirs(assetContainer));
		for (MemoizedFile dir : expectedDirs) {
			if (dir.isDirectory()) {
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
