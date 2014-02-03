package org.bladerunnerjs.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class FileAsset implements Asset {
	private File file;
	private AssetLocation assetLocation;
	
	@Override
	public void initialize(AssetLocation assetLocation, File assetFileOrDir) throws AssetFileInstantationException {
		this.file = assetFileOrDir;
		this.assetLocation = assetLocation;
	}
	
	@Override
	public Reader getReader() throws FileNotFoundException {
		return new BufferedReader(new FileReader(file));
	}
	
	@Override
	public AssetLocation getAssetLocation() {
		return assetLocation;
	}
	
	@Override
	public String getAssetName() {
		return file.getName();
	}
	
	@Override
	public String getAssetPath() {
		return file.getPath();
	}
	
	@Override
	public File getUnderlyingFile() {
		return file;
	}
}
