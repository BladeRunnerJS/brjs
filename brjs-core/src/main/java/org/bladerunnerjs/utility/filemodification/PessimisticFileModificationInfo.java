package org.bladerunnerjs.utility.filemodification;

public class PessimisticFileModificationInfo implements FileModificationInfo {
	private long lastModified = 0;
	
	@Override
	public long getLastModified() {
		return ++lastModified;
	}
}
