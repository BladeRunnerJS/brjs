package org.bladerunnerjs.plugin.plugins.bundlers.cssresource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.FileAsset;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class CssResourceAssetPlugin extends AbstractAssetPlugin {
	private final List<SourceModule> emptySourceModules = new ArrayList<>();
	private final List<LinkedAsset> emptyLinkedAssets = new ArrayList<>();
	private final List<String> resourceExtensions = Arrays.asList(new String[] {"jpg","jpeg","bmp","png","gif","svg","ico","cur","eot","ttf","woff"});
	private BRJS brjs;
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation) {
		return emptySourceModules;
	}
	
	@Override
	public List<LinkedAsset> getLinkedAssets(AssetLocation assetLocation) {
		return emptyLinkedAssets;
	}
	
	@Override
	public List<Asset> getAssets(AssetLocation assetLocation) {
		List<Asset> assets = new ArrayList<>();
		
		try {
			for(String resourceExtension : resourceExtensions) {
				assets.addAll(brjs.createAssetFilesWithExtension(FileAsset.class, assetLocation, resourceExtension));
			}
		}
		catch (AssetFileInstantationException e) {
			throw new RuntimeException(e);
		}
		
		return assets;
	}
}
