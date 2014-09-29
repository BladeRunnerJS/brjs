package org.bladerunnerjs.utility.filemodification;

public class OptimisticFileModificationInfo implements FileModificationInfo {
	private long lastModified;
	private TimeAccessor timeAccessor;
	
	public OptimisticFileModificationInfo(TimeAccessor timeAccessor) {
		this.timeAccessor = timeAccessor;
		resetLastModified();
	}
	
	@Override
	public long getLastModified() {
		return lastModified;
	}
	
	@Override
	public void resetLastModified() {
		lastModified = timeAccessor.getTime();
	}
}
