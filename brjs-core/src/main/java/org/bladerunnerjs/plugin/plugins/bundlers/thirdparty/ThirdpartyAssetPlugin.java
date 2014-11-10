package org.bladerunnerjs.plugin.plugins.bundlers.thirdparty;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class ThirdpartyAssetPlugin extends AbstractAssetPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public boolean canHandleAsset(MemoizedFile assetFile, AssetLocation assetLocation) {
		return ((assetLocation instanceof ThirdpartyAssetLocation) && assetFile.getName().equals("thirdparty-lib.manifest"));
	}
	
	@Override
	public Asset createAsset(MemoizedFile assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		ThirdpartySourceModule sourceModule = new ThirdpartySourceModule((ThirdpartyAssetLocation)assetLocation);
		return sourceModule;
	}
}
