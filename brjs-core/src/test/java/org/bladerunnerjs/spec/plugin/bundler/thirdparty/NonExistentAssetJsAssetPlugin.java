package org.bladerunnerjs.spec.plugin.bundler.thirdparty;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.brjsthirdparty.ThirdpartyAssetLocation;
import org.bladerunnerjs.plugin.plugins.bundlers.nodejs.NodeJsSourceModule;

public class NonExistentAssetJsAssetPlugin extends AbstractAssetPlugin {
	private boolean enabled = false;
	
	public void enable() {
		enabled  = true;
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation) {
		List<SourceModule> sourceModules = new ArrayList<>();
		
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
	public List<LinkedAsset> getLinkedAssets(AssetLocation assetLocation) {
		return new ArrayList<>();
	}
	
	@Override
	public List<Asset> getAssets(AssetLocation assetLocation) {
		return new ArrayList<>();
	}
}
