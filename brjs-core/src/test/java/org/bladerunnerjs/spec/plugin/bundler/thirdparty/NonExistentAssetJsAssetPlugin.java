package org.bladerunnerjs.spec.plugin.bundler.thirdparty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.nodejs.NodeJsSourceModule;
import org.bladerunnerjs.plugin.plugins.bundlers.thirdparty.ThirdpartyAssetLocation;

public class NonExistentAssetJsAssetPlugin extends AbstractAssetPlugin {
	private boolean enabled = false;
	
	public void enable() {
		enabled  = true;
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<Asset> getAssets(AssetLocation assetLocation) {
		List<Asset> sourceModules = new ArrayList<>();
		
		if(enabled && (assetLocation instanceof ThirdpartyAssetLocation)) {
			try {
				sourceModules.add(assetLocation.obtainAsset(NodeJsSourceModule.class, assetLocation.dir(), "non-existent-asset"));
			}
			catch (AssetFileInstantationException e) {
				throw new RuntimeException(e);
			}
		}
		
		return sourceModules;
	}
	
	@Override
	public boolean canHandleAsset(File assetFile, AssetLocation assetLocation) {
		return (enabled && (assetLocation instanceof ThirdpartyAssetLocation));
	}
	
	@Override
	public Asset createAsset(File assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		return new NodeJsSourceModule(assetLocation, assetLocation.dir(), "non-existent-asset");
	}
}
