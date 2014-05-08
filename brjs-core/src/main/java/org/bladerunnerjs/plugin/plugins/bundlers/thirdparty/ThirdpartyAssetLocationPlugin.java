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
	
	public List<String> getSeedAssetLocationDirectories(AssetContainer assetContainer) {
		return new ArrayList<>();
	}
	
	@Override
	public List<String> getAssetLocationDirectories(AssetContainer assetContainer) {
		List<String> assetLocationDirectories = new ArrayList<>();
		
		if((assetContainer instanceof JsLib) && (assetContainer.file("library.manifest").exists())) {
			assetLocationDirectories.add("");
		}
		
		return assetLocationDirectories;
	}
	
	@Override
	public AssetLocation createAssetLocation(AssetContainer assetContainer, String dirPath, Map<String, AssetLocation> assetLocationsMap) {
		return new ThirdpartyAssetLocation(assetContainer.root(), assetContainer, assetContainer.file(dirPath));
	}
	
	@Override
	public boolean allowFurtherProcessing() {
		return false;
	}
}
