package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.model.BRJS;

public interface FileModificationService {
	void initialise(BRJS brjs, File rootDir);
	FileModificationInfo getModificationInfo(File file);
	List<FileModificationInfo> getModificationInfoSet(File[] files);
	void close();
}
