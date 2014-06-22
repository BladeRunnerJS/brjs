package org.bladerunnerjs.utility.filemodification;

import java.io.File;

import org.bladerunnerjs.model.BRJS;

public interface FileModificationService {
	void initialise(BRJS brjs, File rootDir);
	FileModificationInfo getModificationInfo(File file);
	void close();
}
