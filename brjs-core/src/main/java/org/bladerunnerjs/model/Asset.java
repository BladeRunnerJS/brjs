package org.bladerunnerjs.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;

/**
 * Any file that lives within an App
 *
 */
public interface Asset {
	void initialize(AssetLocation assetLocation, File assetFileOrDir) throws AssetFileInstantationException;
	Reader getReader() throws FileNotFoundException;
	AssetLocation getAssetLocation();
	String getAssetName();
	String getAssetPath();
	File getUnderlyingFile(); // TODO: get rid of this method as not all assets will have a direct correlation to a single file
}