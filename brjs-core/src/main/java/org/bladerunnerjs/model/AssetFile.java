package org.bladerunnerjs.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;

public interface AssetFile {
	Reader getReader() throws FileNotFoundException;
	AssetContainer getAssetContainer(); //TODO: remove this - it should be moved to AssetLocation
//	Resources getResources(); //TODO: add this to the interface
	File getUnderlyingFile();
}