package org.bladerunnerjs.plugin.brjsconformant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.InvalidRequirePathException;
import org.bladerunnerjs.api.plugin.AssetRegistry;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.DefaultBladeset;
import org.bladerunnerjs.model.DirectoryAsset;


public class BRJSConformantAssetPlugin extends AbstractAssetPlugin
{

	public static final String IMPLICIT_PACKAGE_USED = "The location '%s' contains a directory with the same name as the parent asset container require prefix ('%s'), using '%s' as the implied require prefix for this location.";

	@Override
	public void discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetRegistry assetDiscoveryInitiator)
	{
		// only create assets if we're at the root of the asset container *and* its not a default bladeset
		if (assetContainer instanceof DefaultBladeset || !neccessaryChildDirsArePresent(assetContainer)
				|| assetContainer.dir() != dir || assetDiscoveryInitiator.hasRegisteredAsset(BRJSConformantRootDirectoryLinkedAsset.calculateRequirePath(assetContainer))) {
			return;
		}

		List<Asset> assets = new ArrayList<>();

		LinkedAsset rootAsset = new BRJSConformantRootDirectoryLinkedAsset(assetContainer);
		assetDiscoveryInitiator.registerAsset(rootAsset);
		assets.add(rootAsset);

		for (MemoizedFile srcDir : getSrcDirs(assetContainer)) {
			if (useImpliedRequirePrefix(assetContainer)) {
				discoverFurtherAssetsForChild(assetContainer, srcDir, requirePrefix, implicitDependencies, assetDiscoveryInitiator, rootAsset);
			} else {
				discoverFurtherAssetsForChild(assetContainer, srcDir, "", implicitDependencies, assetDiscoveryInitiator, rootAsset);
			}
		}

		List<Asset> implicitResourcesDependencies = new ArrayList<>();
		implicitResourcesDependencies.addAll(implicitDependencies);
		implicitResourcesDependencies.add(rootAsset);
		for (MemoizedFile resourceDir : getResourceDirs(assetContainer)) {
			List<Asset> discoveredAssets = createAssetsForChildDir(assetContainer, resourceDir, requirePrefix, implicitResourcesDependencies, assetDiscoveryInitiator, rootAsset);
			rootAsset.addImplicitDependencies(discoveredAssets);
		}

		for (MemoizedFile testDir : getTestDirs(assetContainer)) {
			discoverFurtherAssetsForChild(assetContainer, testDir, "test/"+requirePrefix, implicitDependencies, assetDiscoveryInitiator, rootAsset);
		}

		for (MemoizedFile themeDir : getThemeDirs(assetContainer)) {
			String themeRequirePrefix = "theme!"+themeDir.getName()+":"+requirePrefix;
			List<Asset> discoveredAssets = createAssetsForChildDir(assetContainer, themeDir, themeRequirePrefix, implicitDependencies, assetDiscoveryInitiator, rootAsset);
			rootAsset.addImplicitDependencies(discoveredAssets);
		}
	}

	private boolean useImpliedRequirePrefix(AssetContainer assetContainer) {
		if (assetContainer instanceof JsLib || assetContainer instanceof TestPack) {
			return assetContainer.isNamespaceEnforced();
		}
		return true;
	}

	private String calculateImplicitOrExplicitRequirePrefixDirectory(AssetContainer assetContainer, String srcDirName) {
		MemoizedFile brjsDir = assetContainer.root().dir();
		String rootPath = srcDirName;

		MemoizedFile srcDir = assetContainer.file(srcDirName);
		if (!srcDir.isDirectory()) {
			return srcDirName;
		}

		String rootRequirePrefix = StringUtils.substringBefore(assetContainer.requirePrefix(), "/");
		MemoizedFile rootRequirePrefixDir = srcDir.file(rootRequirePrefix);

		String nestedRequirePrefixPath = srcDirName+"/"+assetContainer.requirePrefix();
		MemoizedFile nestedRequirePrefixDir = assetContainer.file(nestedRequirePrefixPath);

		MemoizedFile rootPathDir = assetContainer.file(rootPath);
		boolean explicitRequirePrefixDirsExist = nestedRequirePrefixDir.isDirectory();

		if (rootRequirePrefixDir.exists() && !explicitRequirePrefixDirsExist) {
			InvalidRequirePathException wrappedRequirePathException = new InvalidRequirePathException(
					String.format("The location '%s' contains a directory with the same name as the root require prefix ('%s') which suggests it's require prefix is explicitly defined"
							+ " but no folder exists that corresponds to the require prefix for this location ('%s'). Either move all source files and package folders into the directory '%s'"
							+ " to use an explicit directory structure or move all files and package folders into '%s' to allow the require prefix to be calculated automatically.",
							brjsDir.getRelativePath(assetContainer.file(srcDirName)),
							rootRequirePrefix,
							assetContainer.requirePrefix(),
							brjsDir.getRelativePath(nestedRequirePrefixDir),
							brjsDir.getRelativePath(rootPathDir)
					));
			throw new RuntimeException(wrappedRequirePathException);

		}

		BRJS brjs = assetContainer.root();
		Logger logger = brjs.logger(this.getClass());
		if (explicitRequirePrefixDirsExist && useImpliedRequirePrefix(assetContainer)) {
			return nestedRequirePrefixPath;
		}
		logger.debug(IMPLICIT_PACKAGE_USED, assetContainer.root().dir().getRelativePath(srcDir), assetContainer.requirePrefix(), assetContainer.requirePrefix());
		return rootPath;
	}

	private List<MemoizedFile> getSrcDirs(AssetContainer assetContainer)
	{
		List<MemoizedFile> dirs = new ArrayList<>();
		if (assetContainer instanceof TestPack) {
			String srcTestDir = calculateImplicitOrExplicitRequirePrefixDirectory(assetContainer, "src-test");
			dirs = createFilesForFilePaths(assetContainer,  Arrays.asList(srcTestDir) );
		} else {
			String srcDir = calculateImplicitOrExplicitRequirePrefixDirectory(assetContainer, "src");
			String srcTestDir = calculateImplicitOrExplicitRequirePrefixDirectory(assetContainer, "src-test");
			dirs = createFilesForFilePaths(assetContainer, Arrays.asList(srcDir, srcTestDir) );
		}
		return filterNonExistantDirs(dirs);
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
		return filterNonExistantDirs(themeDirs);
	}

	private List<MemoizedFile> getResourceDirs(AssetContainer assetContainer)
	{
		List<MemoizedFile> dirs = createFilesForFilePaths(assetContainer, Arrays.asList("resources") );
		return filterNonExistantDirs(dirs);
	}

	private List<MemoizedFile> getTestDirs(AssetContainer assetContainer)
	{
		if (assetContainer instanceof TestPack) {
			List<MemoizedFile> dirs = createFilesForFilePaths(assetContainer, Arrays.asList("tests") );
			return filterNonExistantDirs(dirs);
		} else {
			return Collections.emptyList();
		}
	}

	private List<MemoizedFile> filterNonExistantDirs(List<MemoizedFile> dirs) {
		List<MemoizedFile> existingDirs = new ArrayList<>();
		for (MemoizedFile dir : dirs) {
			if (dir.isDirectory()) {
				existingDirs.add(dir);
			}
		}
		return existingDirs;
	}


	private List<Asset> createAssetsForChildDir(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies,
			AssetRegistry assetDiscoveryInitiator, Asset parentAsset)
	{
		List<Asset> assets = new ArrayList<>();
		Asset child = getOrCreateAsset(assetContainer, dir, requirePrefix, assetDiscoveryInitiator, implicitDependencies, parentAsset);
		assets.add(child);
		assets.addAll( discoverFurtherAssetsForChild(assetContainer, dir, sanitizeChildDirRequirePrefix(child.getPrimaryRequirePath()), implicitDependencies, assetDiscoveryInitiator, child) );
		return assets;
	}

	private String sanitizeChildDirRequirePrefix(String dirAssetRequirePath) {
		if (dirAssetRequirePath.contains("@dir")) {
			return dirAssetRequirePath.replaceFirst("@dir", "");
		}

		return dirAssetRequirePath;
	}

	private List<Asset> discoverFurtherAssetsForChild(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetRegistry assetDiscoveryInitiator, Asset parent)
	{
		List<Asset> furtherAssetImplicitDependencies = new ArrayList<>();
		furtherAssetImplicitDependencies.addAll(implicitDependencies);
		furtherAssetImplicitDependencies.add(parent);

		List<Asset> discoveredAssets = assetDiscoveryInitiator.discoverFurtherAssets(dir, requirePrefix, furtherAssetImplicitDependencies);
		List<Asset> dependentAssets = new ArrayList<>();
		if (parent instanceof LinkedAsset) {
			for (Asset dependentAsset : discoveredAssets) {
				if (dependentAsset instanceof SourceModule) {
					continue;
				}
				dependentAssets.add(dependentAsset);
			}
			((LinkedAsset) parent).addImplicitDependencies(dependentAssets);
		}

		for (MemoizedFile childDir : dir.dirs()) {
			discoveredAssets.addAll( createAssetsForChildDir(assetContainer, childDir, requirePrefix, implicitDependencies, assetDiscoveryInitiator, parent) );
		}
		return discoveredAssets;
	}

	private Asset getOrCreateAsset(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, AssetRegistry assetDiscoveryInitiator, List<Asset> implicitDependencies, Asset parent) {
		String requirePath = DirectoryAsset.getRequirePath(requirePrefix, dir);

		if (!assetDiscoveryInitiator.hasRegisteredAsset(requirePath)) {
			List<Asset> furtherAssetImplicitDependencies = new ArrayList<>();
			furtherAssetImplicitDependencies.addAll(implicitDependencies);
			furtherAssetImplicitDependencies.add(parent);
			Asset asset = new DirectoryAsset(assetContainer, dir, requirePrefix, furtherAssetImplicitDependencies);
			assetDiscoveryInitiator.registerAsset(asset);
			return asset;
		} else {
			return assetDiscoveryInitiator.getRegisteredAsset( requirePath );
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
		expectedDirs.addAll(getThemeDirs(assetContainer));
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
