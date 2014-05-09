package org.bladerunnerjs.plugin.plugins.bundlers.cssresource;

import java.io.File;
import java.util.regex.Pattern;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.FileAsset;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class CssResourceAssetPlugin extends AbstractAssetPlugin {
	private final Pattern fileExtensions = Pattern.compile("(jpg|jpeg|bmp|png|gif|svg|ico|cur|eot|ttf|woff)$");
	
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}
	
	@Override
	public boolean canHandleAsset(File assetFile, AssetLocation assetLocation) {
		return fileExtensions.matcher(assetFile.getName()).matches();
	}
	
	@Override
	public Asset createAsset(File assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		return new FileAsset(assetLocation, assetFile.getParentFile(), assetFile.getName());
	}
}
