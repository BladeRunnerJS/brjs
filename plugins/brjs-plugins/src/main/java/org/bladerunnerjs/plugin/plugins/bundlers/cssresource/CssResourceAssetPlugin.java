package org.bladerunnerjs.plugin.plugins.bundlers.cssresource;

import java.util.regex.Pattern;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.FileAsset;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class CssResourceAssetPlugin extends AbstractAssetPlugin {
	private final Pattern fileExtensions = Pattern.compile("(jpg|jpeg|bmp|png|gif|svg|ico|cur|eot|ttf|woff)$");
	
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}
	
	@Override
	public boolean canHandleAsset(MemoizedFile assetFile, AssetLocation assetLocation) {
		return fileExtensions.matcher(assetFile.getName()).matches();
	}
	
	@Override
	public Asset createAsset(MemoizedFile assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		return new FileAsset(assetFile, assetLocation);
	}
}
