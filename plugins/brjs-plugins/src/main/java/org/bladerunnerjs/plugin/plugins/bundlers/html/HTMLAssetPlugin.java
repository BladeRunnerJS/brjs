package org.bladerunnerjs.plugin.plugins.bundlers.html;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.LinkedFileAsset;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class HTMLAssetPlugin extends AbstractAssetPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public boolean canHandleAsset(MemoizedFile assetFile, AssetLocation assetLocation) {
		return assetFile.getName().endsWith(".html");
	}
	
	@Override
	public Asset createAsset(MemoizedFile assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		return new LinkedFileAsset(assetFile, assetLocation);
	}
}
