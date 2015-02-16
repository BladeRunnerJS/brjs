package org.bladerunnerjs.plugin.bundlers.xml;

import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.AssetDiscoveryInitiator;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetContainer;

public class XMLAssetPlugin extends AbstractAssetPlugin {
	
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}

	@Override
	public void discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		if (!requirePrefix.startsWith("xml!")) {
			requirePrefix = "xml!"+requirePrefix;
		}
		
		IOFileFilter noAliasesFileFilter = new NotFileFilter( new NameFileFilter( Arrays.asList("aliases.xml", "aliasDefinitions.xml") ) );
		FileFilter htmlFileFilter = new AndFileFilter( (IOFileFilter) new SuffixFileFilter(".xml"), noAliasesFileFilter );
		for (MemoizedFile xmlFile : dir.listFiles(htmlFileFilter)) {
			XMLAsset asset = new XMLAsset(xmlFile, assetContainer, requirePrefix);
			if (!assetDiscoveryInitiator.hasRegisteredAsset(asset.getPrimaryRequirePath())) {
				if (dir.isChildOf(assetContainer.file("resources"))) {
					assetDiscoveryInitiator.registerSeedAsset( asset );
				} else {
					assetDiscoveryInitiator.registerAsset( asset );
				}
			}
		}
	}
}
