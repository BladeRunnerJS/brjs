package org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.UnhandledAssetContainerException;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class NamespacedJsAssetPlugin extends AbstractAssetPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<AssetLocation> getAssetLocations(AssetContainer assetContainer) throws UnhandledAssetContainerException {
		throw new UnhandledAssetContainerException();
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation, List<File> files) {
		try {
			if (assetLocation.getJsStyle().equals(NamespacedJsBundlerContentPlugin.JS_STYLE)) {
				return assetLocation.getAssetContainer().root().createAssetFilesWithExtension(NamespacedJsSourceModule.class, assetLocation, files, "js");
			}
			else {
				return new ArrayList<>();
			}
		}
		catch (AssetFileInstantationException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<LinkedAsset> getLinkedResourceFiles(AssetLocation assetLocation, List<File> files) {
		return new ArrayList<>();
	}
	
	@Override
	public List<Asset> getResourceFiles(AssetLocation assetLocation, List<File> files) {
		return new ArrayList<>();
	}
}
