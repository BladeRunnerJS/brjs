package org.bladerunnerjs.utility.filemodification;

import java.io.File;

public class OptimisticFileModificationService implements FileModificationService {
	OptimisticFileModificationInfo optimisticFileModificationInfo = new OptimisticFileModificationInfo();
	
	@Override
	public void setRootDir(File rootDir) {
		// do nothing
	}
	
	@Override
	public FileModificationInfo getModificationInfo(File file) {
		return optimisticFileModificationInfo;
	}
	
	@Override
	public void close() {
		// do nothing
	}
}
