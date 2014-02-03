package org.bladerunnerjs.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;

public class DeepAsset implements Asset {
	private final Asset asset;
	private final DeepAssetLocation deepAssetLocation;
	
	public DeepAsset(Asset asset, DeepAssetLocation deepAssetLocation) {
		this.asset = asset;
		this.deepAssetLocation = deepAssetLocation;
	}
	
	public void initialize(AssetLocation assetLocation, File assetFileOrDir) throws AssetFileInstantationException {
		asset.initialize(assetLocation, assetFileOrDir);
	}
	
	public Reader getReader() throws FileNotFoundException {
		return asset.getReader();
	}
	
	public AssetLocation getAssetLocation() {
		return deepAssetLocation;
	}
	
	public String getAssetName() {
		return asset.getAssetName();
	}
	
	public String getAssetPath() {
		return asset.getAssetPath();
	}
	
	public File getUnderlyingFile() {
		return asset.getUnderlyingFile();
	}
}
