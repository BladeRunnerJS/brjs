package org.bladerunnerjs.plugin.brjsconformant;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.InvalidRequirePathException;
import org.bladerunnerjs.api.plugin.AssetDiscoveryInitiator;
import org.bladerunnerjs.api.plugin.base.AbstractAssetLocationPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.DefaultBladeset;
import org.bladerunnerjs.model.DirectoryLinkedAsset;


public class BRJSConformantAssetLocationPlugin extends AbstractAssetLocationPlugin
{

	@Override
	public void discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		if (assetContainer instanceof DefaultBladeset) {
			return;
		}
		
		if (assetContainer.dir() == dir) {
			List<String> childPaths = Arrays.asList("src", "src-test", "themes");
			for (String childPath : childPaths) {
				MemoizedFile childDir = dir.file(childPath);
				LinkedAsset child = new DirectoryLinkedAsset(assetContainer, childDir, requirePrefix, null);
				assetDiscoveryInitiator.registerAsset(child);
				assetDiscoveryInitiator.discoverFurtherAssets(childDir, requirePrefix, implicitDependencies);
			}
		} else if (assetContainer.file("resources") == dir) {
			// TODO: should this be a 'deep' directory asset
			LinkedAsset child = new DirectoryLinkedAsset(assetContainer, assetContainer.file("resources"), requirePrefix, null);
			assetDiscoveryInitiator.registerAsset(child);
		} else {
//			LinkedAsset parentLinkedAsset = assetContainer.linkedAsset(requirePrefix);
			LinkedAsset parentLinkedAsset = null;
			for (MemoizedFile childDir : dir.dirs()) {
				String childDirRequirePath = calculateChildRequirePrefix(assetContainer, childDir);
				LinkedAsset child = new DirectoryLinkedAsset(assetContainer, childDir, childDirRequirePath, parentLinkedAsset);
				assetDiscoveryInitiator.registerAsset(child);				
				assetDiscoveryInitiator.discoverFurtherAssets(childDir, child.getPrimaryRequirePath(), implicitDependencies);
			}
		}
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
	}

	
	static String calculateChildRequirePrefix(AssetContainer assetContainer, MemoizedFile childDir)
	{
		String assetContainerRequirePrefix = assetContainer.requirePrefix();
		
		if (childDir.getParentFile() == assetContainer.dir()) {
			return assetContainerRequirePrefix;
		}
		
		String childPathRelativeToAssetContainer = assetContainer.dir().getRelativePath(childDir);
		String requirePathRelativeToAssetContainer = StringUtils.substringAfter(childPathRelativeToAssetContainer, "/");
		String appRequirePrefix = assetContainer.app().getRequirePrefix();
		
		String expectedRequirePrefix = StringUtils.substring( assetContainerRequirePrefix, 0, requirePathRelativeToAssetContainer.length() );
		if (requirePathRelativeToAssetContainer.startsWith(appRequirePrefix) && !requirePathRelativeToAssetContainer.startsWith(expectedRequirePrefix)) {
			InvalidRequirePathException wrappedRequirePathException = new InvalidRequirePathException(
					"The source module directory at '"+assetContainer.root().dir().getRelativePath(childDir)+"' is in an invalid location. "+
					"It's require path starts with the app's require prefix ('"+appRequirePrefix+"') which suggests it's require path is intended to be '"+assetContainerRequirePrefix+"/...' "+
					"The require path defined by the directory is '"+requirePathRelativeToAssetContainer+"'. Either it's package structure should be '"+assetContainerRequirePrefix+"/*' or "+
					"remove the folders '"+requirePathRelativeToAssetContainer+"' to allow the require prefix to be calculated automatically.");
			throw new RuntimeException(wrappedRequirePathException);
		}		
		
		if (requirePathRelativeToAssetContainer.startsWith(appRequirePrefix)) {
			requirePathRelativeToAssetContainer = StringUtils.substringAfter(requirePathRelativeToAssetContainer, assetContainerRequirePrefix).replaceFirst("/", "");
		}
		
		return assetContainerRequirePrefix+"/"+requirePathRelativeToAssetContainer;
	}	
	
	
}
