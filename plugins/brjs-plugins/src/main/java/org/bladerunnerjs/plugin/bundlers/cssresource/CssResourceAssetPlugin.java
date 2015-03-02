package org.bladerunnerjs.plugin.bundlers.cssresource;

import java.util.regex.Pattern;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.FileAsset;

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
