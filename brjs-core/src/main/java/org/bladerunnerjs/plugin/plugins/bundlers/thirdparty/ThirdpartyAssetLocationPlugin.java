package org.bladerunnerjs.plugin.plugins.bundlers.thirdparty;

import java.io.File;
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
	public List<File> getAssetLocationDirectories(AssetContainer assetContainer) {
		List<File> assetLocationDirectories = new ArrayList<>();
		
		if((assetContainer instanceof JsLib) && (assetContainer.file("library.manifest").exists())) {
			assetLocationDirectories.add(assetContainer.dir());
		}
		
		return assetLocationDirectories;
	}
	
	public List<File> getSeedAssetLocationDirectories(AssetContainer assetContainer) {
		return new ArrayList<>();
	}
	
	@Override
	public AssetLocation createAssetLocation(AssetContainer assetContainer, File dir, Map<String, AssetLocation> assetLocationsMap) {
		return new ThirdpartyAssetLocation(assetContainer.root(), assetContainer, dir);
	}
	
	@Override
	public boolean allowFurtherProcessing() {
		return false;
	}
}
