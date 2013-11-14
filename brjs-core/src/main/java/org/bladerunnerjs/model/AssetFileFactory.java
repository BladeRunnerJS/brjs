package org.bladerunnerjs.model;

import java.io.File;

public interface AssetFileFactory<AF extends AssetFile> {
	AF createFile(AssetContainer assetContainer, File file);
}
