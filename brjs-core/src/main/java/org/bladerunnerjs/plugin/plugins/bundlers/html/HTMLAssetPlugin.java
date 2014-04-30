package org.bladerunnerjs.plugin.plugins.bundlers.html;

import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.FullyQualifiedLinkedAsset;
import org.bladerunnerjs.model.SuffixAssetFilter;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class HTMLAssetPlugin extends AbstractAssetPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<Asset> getAssets(AssetLocation assetLocation) {
		List<Asset> assets;
		
		try {
			assets = assetLocation.obtainMatchingAssets(new SuffixAssetFilter("html"), Asset.class, FullyQualifiedLinkedAsset.class);
		}
		catch (AssetFileInstantationException e) {
			throw new RuntimeException(e);
		}
		
		return assets;
	}
}
