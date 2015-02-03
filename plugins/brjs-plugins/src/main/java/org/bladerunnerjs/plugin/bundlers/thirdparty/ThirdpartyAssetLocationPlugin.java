package org.bladerunnerjs.plugin.bundlers.thirdparty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.ThirdpartyLibManifest;
import org.bladerunnerjs.api.plugin.base.AbstractLegacyAssetLocationPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.plugin.brjsconformant.BRJSConformantLegacyAssetLocationPlugin;

public class ThirdpartyAssetLocationPlugin extends AbstractLegacyAssetLocationPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return Arrays.asList(BRJSConformantLegacyAssetLocationPlugin.class.getCanonicalName());
	}
	
	@Override
	public List<String> getAssetLocationDirectories(AssetContainer assetContainer) {
		List<String> assetLocationDirectories = new ArrayList<>();
		
		if((assetContainer instanceof JsLib) && (assetContainer.file( ThirdpartyLibManifest.LIBRARY_MANIFEST_FILENAME ).exists())) {
			assetLocationDirectories.add(".");
		}
		
		return assetLocationDirectories;
	}
	
	public List<String> getSeedAssetLocationDirectories(AssetContainer assetContainer) {
		return Collections.emptyList();
	}
	
	@Override
	public AssetLocation createAssetLocation(AssetContainer assetContainer, String dirPath, Map<String, AssetLocation> assetLocationsMap) {
		return new ThirdpartyAssetLocation(assetContainer.root(), assetContainer, assetContainer.file(dirPath), assetLocationsMap.get("."));
	}
	
	@Override
	public boolean allowFurtherProcessing() {
		return false;
	}
}
