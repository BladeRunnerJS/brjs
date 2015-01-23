package org.bladerunnerjs.plugin.plugins.bundlers.thirdparty;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetFileInstantationException;
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
