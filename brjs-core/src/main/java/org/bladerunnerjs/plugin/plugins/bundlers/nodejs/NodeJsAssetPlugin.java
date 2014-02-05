package org.bladerunnerjs.plugin.plugins.bundlers.nodejs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class NodeJsAssetPlugin extends AbstractAssetPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation) {
		try {
			if (assetLocation.getJsStyle().equals(NodeJsContentPlugin.JS_STYLE)) {
				return assetLocation.obtainMatchingAssets(new SuffixFileFilter("js"), SourceModule.class, NodeJsSourceModule.class);
			}
			else {
				return new ArrayList<>();
			}
		}
		catch (AssetFileInstantationException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<LinkedAsset> getLinkedAssets(AssetLocation assetLocation) {
		if (assetLocation.getAssetContainer() instanceof TestPack)
		{
			return new ArrayList<LinkedAsset>( getSourceModules(assetLocation) );
		}
		return new ArrayList<>();
	}
	
	@Override
	public List<Asset> getAssets(AssetLocation assetLocation) {
		return Arrays.asList();
	}
}
