package org.bladerunnerjs.model;

import java.io.File;

public interface FileSetFactory<AF extends AssetFile> {
	AF createFile(File filePath);
}
