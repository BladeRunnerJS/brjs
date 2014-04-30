package org.bladerunnerjs.plugin.plugins.bundlers.css;

import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.FileAsset;
import org.bladerunnerjs.model.SuffixAssetFilter;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class CssAssetPlugin extends AbstractAssetPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}
	
	@Override
	public List<Asset> getAssets(AssetLocation assetLocation) {
		try {
			return assetLocation.obtainMatchingAssets(new SuffixAssetFilter("css"), Asset.class, FileAsset.class);
		}
		catch (AssetFileInstantationException e) {
			throw new RuntimeException(e);
		}
	}
}
