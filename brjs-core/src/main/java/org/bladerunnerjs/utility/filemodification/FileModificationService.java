package org.bladerunnerjs.utility.filemodification;

import java.io.File;

import org.bladerunnerjs.model.engine.RootNode;

public interface FileModificationService {
	void initialise(File rootDir, TimeAccessor timeAccessor, RootNode rootNode);
	FileModificationInfo getFileModificationInfo(File file);
	FileModificationInfo getFileSetModificationInfo(File file, File primarySetFile);
	void close();
}
