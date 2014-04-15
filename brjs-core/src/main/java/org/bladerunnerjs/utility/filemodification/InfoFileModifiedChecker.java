package org.bladerunnerjs.utility.filemodification;

public class InfoFileModifiedChecker implements FileModifiedChecker {
	private FileModificationInfo fileModificationInfo;
	private long lastModifiedTime = -1;
	
	public InfoFileModifiedChecker(FileModificationInfo fileModificationInfo) {
		this.fileModificationInfo = fileModificationInfo;
	}
	
	@Override
	public boolean hasChangedSinceLastCheck() {
		long newLastModified = fileModificationInfo.getLastModified();
		boolean hasChangedSinceLastCheck = (newLastModified > lastModifiedTime);
		lastModifiedTime = newLastModified;
		
		return hasChangedSinceLastCheck;
	}
}
