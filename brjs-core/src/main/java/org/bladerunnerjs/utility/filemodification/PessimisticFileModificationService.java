package org.bladerunnerjs.utility.filemodification;

import java.io.File;

public class PessimisticFileModificationService implements FileModificationService {
	PessimisticFileModificationInfo pessimisticFileModificationInfo = new PessimisticFileModificationInfo();
	
	@Override
	public FileModificationInfo getModificationInfo(File file) {
		return pessimisticFileModificationInfo;
	}
	
	@Override
	public void close() {
		// do nothing
	}
}
