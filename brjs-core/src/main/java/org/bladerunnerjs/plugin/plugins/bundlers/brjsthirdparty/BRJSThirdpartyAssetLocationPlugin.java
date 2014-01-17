package org.bladerunnerjs.plugin.plugins.bundlers.brjsthirdparty;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.plugin.base.AbstractAssetLocationPlugin;

public class BRJSThirdpartyAssetLocationPlugin extends AbstractAssetLocationPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public boolean canHandleAssetContainer(AssetContainer assetContainer) {
		return ((assetContainer instanceof JsLib) && (assetContainer.file("library.manifest").exists()));
	}
	
	@Override
	public List<AssetLocation> getAssetLocations(AssetContainer assetContainer) {
		List<AssetLocation> assetLocations = null;
		
		assetLocations = new ArrayList<>();
		assetLocations.add(new ThirdpartyAssetLocation(assetContainer.root(), assetContainer, assetContainer.dir()));
		
		return assetLocations;
	}
}
