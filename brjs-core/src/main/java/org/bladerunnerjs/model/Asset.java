package org.bladerunnerjs.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;

/**
 * Any file that lives within an App
 *
 */
public interface Asset {
	Reader getReader() throws FileNotFoundException;
	AssetLocation getAssetLocation();
	String getAssetName();
	String getAssetPath();
	File getUnderlyingFile(); // TODO: get rid of this method as not all assets will have a direct correlation to a single file
	
	//TODO: remove this if we decide we're happy to use a non-default constructor in AssetLocationUtility
	void initializeUnderlyingObjects(AssetLocation assetLocation, File file);
}