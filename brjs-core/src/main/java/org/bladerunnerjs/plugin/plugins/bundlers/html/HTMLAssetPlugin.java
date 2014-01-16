package org.bladerunnerjs.plugin.plugins.bundlers.html;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.FullyQualifiedLinkedAsset;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class HTMLAssetPlugin extends AbstractAssetPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation) {
		return new ArrayList<>();
	}
	
	@Override
	public List<LinkedAsset> getLinkedAssets(AssetLocation assetLocation) {
		try {
			return assetLocation.getAssetContainer().root().createAssetFilesWithExtension(FullyQualifiedLinkedAsset.class, assetLocation, "html");
		}
		catch (AssetFileInstantationException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<Asset> getAssets(AssetLocation assetLocation) {
		return new ArrayList<>();
	}
}
