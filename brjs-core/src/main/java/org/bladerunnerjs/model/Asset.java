package org.bladerunnerjs.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;

/**
 * Any file that lives within an App
 *
 */
public interface Asset {
	void initialize(AssetLocation assetLocation, File dir, String assetName) throws AssetFileInstantationException;
	Reader getReader() throws FileNotFoundException;
	AssetLocation getAssetLocation();
	File dir();
	String getAssetName();
	String getAssetPath();
}