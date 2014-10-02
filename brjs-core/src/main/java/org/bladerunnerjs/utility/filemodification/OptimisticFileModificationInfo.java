package org.bladerunnerjs.utility.filemodification;

public class OptimisticFileModificationInfo implements FileModificationInfo {
	private long lastModified;
	private final FileModificationInfo parent;
	private final TimeAccessor timeAccessor;
	
	public OptimisticFileModificationInfo(FileModificationInfo parent, TimeAccessor timeAccessor) {
		this.parent = parent;
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
		
		if(parent != null) {
			parent.resetLastModified();
		}
	}
}
