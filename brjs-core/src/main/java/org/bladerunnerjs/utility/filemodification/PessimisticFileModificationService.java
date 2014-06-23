package org.bladerunnerjs.utility.filemodification;

import java.io.File;

import org.bladerunnerjs.model.BRJS;

public class PessimisticFileModificationService implements FileModificationService {
	PessimisticFileModificationInfo pessimisticFileModificationInfo = new PessimisticFileModificationInfo();
	
	@Override
	public void initialise(BRJS brjs, File rootDir) {
		// do nothing
	}
	
	@Override
	public FileModificationInfo getModificationInfo(File file) {
		return pessimisticFileModificationInfo;
	}
	
	@Override
	public void close() {
		// do nothing
	}
}
