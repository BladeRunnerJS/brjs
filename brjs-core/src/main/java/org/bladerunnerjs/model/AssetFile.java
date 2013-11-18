package org.bladerunnerjs.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;

/**
 * Any file that lives within an App
 *
 */
public interface AssetFile {
	Reader getReader() throws FileNotFoundException;
	AssetLocation getAssetLocation();
	File getUnderlyingFile();
	
	
	//TODO: remove this if we decide we're happy to use a non-default constructor in AssetLocationUtility
	void initializeUnderlyingObjects(AssetLocation assetLocation, File file);
}