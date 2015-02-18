package org.bladerunnerjs.plugin.bundlers.xml;

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	
	IOFileFilter noAliasesFileFilter = new NotFileFilter( new NameFileFilter( Arrays.asList("aliases.xml", "aliasDefinitions.xml") ) );
	FileFilter xmlFileFilter = new AndFileFilter( (IOFileFilter) new SuffixFileFilter(".xml"), noAliasesFileFilter );
	
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}

	@Override
	public List<Asset> discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		if (assetContainer.dir() == dir) {
			return Collections.emptyList();
		}
		
		if (!requirePrefix.startsWith("xml!")) {
			requirePrefix = "xml!"+requirePrefix;
		}
		
		List<Asset> assets = new ArrayList<>();
		for (MemoizedFile xmlFile : dir.listFiles(xmlFileFilter)) {
			if (!assetDiscoveryInitiator.hasRegisteredAsset(XMLAsset.calculateRequirePath(requirePrefix, xmlFile))) {
				XMLAsset asset = new XMLAsset(xmlFile, assetContainer, requirePrefix, implicitDependencies);
				assets.add(asset);
				if (dir.isChildOf(assetContainer.file("resources"))) {
					assetDiscoveryInitiator.registerSeedAsset( asset );
				} else {
					assetDiscoveryInitiator.registerAsset( asset );
				}
			}
		}
		return assets;
	}
}
