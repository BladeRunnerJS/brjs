package org.bladerunnerjs.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;

public interface AssetFile {
	Reader getReader() throws FileNotFoundException;
	SourceLocation getSourceLocation();
	File getUnderlyingFile();
}
