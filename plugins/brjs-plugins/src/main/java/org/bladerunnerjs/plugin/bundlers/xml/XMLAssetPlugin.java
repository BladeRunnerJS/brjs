package org.bladerunnerjs.plugin.bundlers.xml;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetFileInstantationException;

public class XMLAssetPlugin extends AbstractAssetPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}
	
	@Override
	public boolean canHandleAsset(MemoizedFile assetFile, AssetLocation assetLocation) {
		String assetName = assetFile.getName();
		// TODO: should the aliases xml filtering be moved into the model once getAssets() has been deleted?
		return (assetName.endsWith(".xml") && !assetName.equals("aliases.xml")  && !assetName.equals("aliasDefinitions.xml"));
	}
	
	@Override
	public Asset createAsset(MemoizedFile assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		return new XMLAsset(assetFile, assetLocation);
	}
}
