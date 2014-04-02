package org.bladerunnerjs.utility.filemodification;

public class OptimisticFileModificationInfo implements FileModificationInfo {
	@Override
	public long getLastModified() {
		return 1;
	}
	
	@Override
	public void resetLastModified() {
		// do nothing
	}
}
