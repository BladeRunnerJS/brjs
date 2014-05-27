package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

/**
 * Any file that lives within an App
 *
 */
public interface Asset {
	Reader getReader() throws IOException;
	AssetLocation assetLocation();
	File dir();
	String getAssetName();
	String getAssetPath();
}