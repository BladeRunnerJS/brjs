package org.bladerunnerjs.plugin.seedlocator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetDiscoveryInitiator;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.api.BrowsableNode;
import org.bladerunnerjs.model.IndexPageAsset;


public class BrowsableNodeSeedLocator extends AbstractAssetPlugin
{
	
	@Override
	public void setBRJS(BRJS brjs)
	{	
	}
	
	@Override
	public List<Asset> discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		if (assetContainer instanceof BrowsableNode && assetContainer.dir() == dir) {
			MemoizedFile indexFile = null;
			
			if(assetContainer.file("index.html").isFile()) {
				indexFile = assetContainer.file("index.html");
			}
			else if(assetContainer.file("index.jsp").exists()) {
				indexFile = assetContainer.file("index.jsp");
			}
			
			if (indexFile != null) {
				LinkedAsset indexAsset = new IndexPageAsset(indexFile, assetContainer, requirePrefix, implicitDependencies);
				assetDiscoveryInitiator.registerSeedAsset(indexAsset);
				return Arrays.asList(indexAsset);
			}			
		}
		return Collections.emptyList();
	}

}
