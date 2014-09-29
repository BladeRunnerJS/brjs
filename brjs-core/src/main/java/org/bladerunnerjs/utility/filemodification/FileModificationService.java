package org.bladerunnerjs.utility.filemodification;

import java.io.File;

import org.bladerunnerjs.model.FileInfoAccessor;

public interface FileModificationService {
	void initialise(File rootDir, TimeAccessor timeAccessor, FileInfoAccessor fileInfoAccessor);
	FileModificationInfo getFileModificationInfo(File file);
	FileModificationInfo getFileSetModificationInfo(File file, File primarySetFile);
	void close();
}
