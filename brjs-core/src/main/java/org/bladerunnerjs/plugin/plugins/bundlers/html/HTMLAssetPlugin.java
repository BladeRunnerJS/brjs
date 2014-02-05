package org.bladerunnerjs.plugin.plugins.bundlers.html;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.FullyQualifiedLinkedAsset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class HTMLAssetPlugin extends AbstractAssetPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation) {
		return new ArrayList<>();
	}
	
	@Override
	public List<LinkedAsset> getLinkedAssets(AssetLocation assetLocation) {
		List<LinkedAsset> assets;
		
		try {
			AssetContainer assetContainer = assetLocation.getAssetContainer();
			
			if(assetContainer instanceof JsLib) {
				assets = new ArrayList<>();
			}
			else {
				assets = assetLocation.obtainMatchingAssets(new SuffixFileFilter("html"), LinkedAsset.class, FullyQualifiedLinkedAsset.class);
			}
		}
		catch (AssetFileInstantationException e) {
			throw new RuntimeException(e);
		}
		
		return assets;
	}
	
	@Override
	public List<Asset> getAssets(AssetLocation assetLocation) {
		List<Asset> result = new ArrayList<Asset>();
		result.addAll(this.getLinkedAssets(assetLocation));
		return result;
	}
}
