package org.bladerunnerjs.utility.filemodification;

import java.io.File;

public interface FileModificationService {
	FileModificationInfo getModificationInfo(File file);
	void close();
}
