package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

/**
 * Any file that lives within an App
 *
 */
public interface Asset {
	void initialize(AssetLocation assetLocation, File dir, String assetName) throws AssetFileInstantationException;
	Reader getReader() throws IOException;
	AssetLocation assetLocation();
	File dir();
	String getAssetName();
	String getAssetPath();
}