package org.bladerunnerjs.plugin.brjsconformant;

import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
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
		if (assetContainer.dir() != dir || assetContainer instanceof DefaultBladeset) {
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
			LinkedAsset parentLinkedAsset = assetContainer.linkedAsset(requirePrefix);
			for (MemoizedFile childDir : dir.dirs()) {
				LinkedAsset child = new DirectoryLinkedAsset(assetContainer, childDir, requirePrefix, parentLinkedAsset);
				assetDiscoveryInitiator.registerAsset(child);
				String childRequirePrefix = ((requirePrefix.equals("")) ? "" : requirePrefix+"/") + childDir.getName();
				assetDiscoveryInitiator.discoverFurtherAssets(childDir, childRequirePrefix, implicitDependencies);
			}
		}
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
	}

}
