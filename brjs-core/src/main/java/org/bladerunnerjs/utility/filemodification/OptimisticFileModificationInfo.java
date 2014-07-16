package org.bladerunnerjs.utility.filemodification;

public class OptimisticFileModificationInfo implements FileModificationInfo {
	private long lastModified = 1;
	
	@Override
	public long getLastModified() {
		return lastModified;
	}
	
	@Override
	public void resetLastModified() {
		lastModified++;
	}
}
