package org.bladerunnerjs.spec.bundling.aspect;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.SourceAssetLocation;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.nodejs.NodeJsSourceModule;

public class InvalidAssetPlugin extends AbstractAssetPlugin {
	private boolean enabled;
	
	public void enable() {
		enabled = true;
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<Asset> getAssets(AssetLocation assetLocation) {
		List<Asset> sourceModules = new ArrayList<>();
		
		if(enabled && (assetLocation instanceof SourceAssetLocation)) {
			try {
				sourceModules.add(assetLocation.obtainAsset(NodeJsSourceModule.class, assetLocation.file("pkg2"), "SomeClass.js"));
			}
			catch (AssetFileInstantationException e) {
				throw new RuntimeException(e);
			}
		}
		
		return sourceModules;
	}
	
	@Override
	public boolean canHandleAsset(File assetFile, AssetLocation assetLocation) {
		return (enabled && (assetLocation instanceof SourceAssetLocation));
	}
	
	@Override
	public Asset createAsset(File assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		return new NodeJsSourceModule(assetLocation, assetLocation.file("pkg2"), "SomeClass.js");
	}
}
