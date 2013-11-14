package org.bladerunnerjs.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;

public interface AssetFile {
	Reader getReader() throws FileNotFoundException;
	AssetContainer getAssetContainer(); // TODO: this should be change to getAssetLocation(), since you can always get the AssetContainer from the AssetLocation, but not the other way around.
	File getUnderlyingFile();
}