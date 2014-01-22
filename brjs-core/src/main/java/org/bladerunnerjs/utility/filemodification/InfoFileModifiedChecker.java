package org.bladerunnerjs.utility.filemodification;

public class InfoFileModifiedChecker implements FileModifiedChecker {
	private FileModificationInfo fileModificationInfo;
	private long lastModifiedTime = 0;
	
	public InfoFileModifiedChecker(FileModificationInfo fileModificationInfo) {
		this.fileModificationInfo = fileModificationInfo;
	}
	
	@Override
	public boolean hasChangedSinceLastCheck() {
		boolean hasChangedSinceLastCheck = (fileModificationInfo.getLastModified() > lastModifiedTime);
		lastModifiedTime = fileModificationInfo.getLastModified();
		
		return hasChangedSinceLastCheck;
	}
}
