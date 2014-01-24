package org.bladerunnerjs.plugin.plugins.bundlers.typescript;

import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class TypeScriptAssetPlugin extends AbstractAssetPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation) {
		try {
			return assetLocation.getAssetContainer().root().createAssetFilesWithExtension(TypeScriptSourceModule.class, assetLocation, "ts");
		}
		catch (AssetFileInstantationException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<LinkedAsset> getLinkedAssets(AssetLocation assetLocation) {
		return Arrays.asList();
	}
	
	@Override
	public List<Asset> getAssets(AssetLocation assetLocation) {
		return Arrays.asList();
	}
}
