package org.bladerunnerjs.utility.filemodification;

import java.io.File;

public interface FileModificationService {
	void setRootDir(File rootDir);
	FileModificationInfo getModificationInfo(File file);
	void close();
}
