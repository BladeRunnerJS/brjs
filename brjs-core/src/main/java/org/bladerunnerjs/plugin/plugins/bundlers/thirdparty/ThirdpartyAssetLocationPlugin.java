package org.bladerunnerjs.plugin.plugins.bundlers.thirdparty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.plugin.base.AbstractAssetLocationPlugin;
import org.bladerunnerjs.plugin.plugins.brjsconformant.BRJSConformantAssetLocationPlugin;

public class ThirdpartyAssetLocationPlugin extends AbstractAssetLocationPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<String> getPluginsThatMustAppearBeforeThisPlugin() {
		return new ArrayList<>();
	}
	
	@Override
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return Arrays.asList(BRJSConformantAssetLocationPlugin.class.getCanonicalName());
	}
	
	@Override
	public boolean canHandleAssetContainer(AssetContainer assetContainer) {
		return ((assetContainer instanceof JsLib) && (assetContainer.file("library.manifest").exists()));
	}
	
	@Override
	public List<AssetLocation> getAssetLocations(AssetContainer assetContainer, Map<String, AssetLocation> assetLocationCache) {
		if(!assetLocationCache.containsKey("root")) {
			assetLocationCache.put("root", new ThirdpartyAssetLocation(assetContainer.root(), assetContainer, assetContainer.dir()));
		}
		
		List<AssetLocation> assetLocations = new ArrayList<>();
		assetLocations.add(assetLocationCache.get("root"));
		
		return assetLocations;
	}
	
	@Override
	public boolean allowFurtherProcessing() {
		return false;
	}
}
