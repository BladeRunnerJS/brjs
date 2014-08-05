package org.bladerunnerjs.utility.filemodification;

import org.bladerunnerjs.model.FileInfo;

public class PrimaryFileModificationInfo implements FileModificationInfo {
	private final FileInfo fileInfo;
	private final FileModificationInfo fileModificationInfo;
	private boolean isExistentFile;
	
	public PrimaryFileModificationInfo(FileInfo fileInfo, FileModificationInfo fileModificationInfo) {
		this.fileInfo = fileInfo;
		this.fileModificationInfo = fileModificationInfo;
	}
	
	@Override
	public long getLastModified() {
		isExistentFile = fileInfo.exists() && !fileInfo.isDirectory();
		return fileModificationInfo.getLastModified();
	}
	
	@Override
	public void resetLastModified() {
		fileModificationInfo.resetLastModified();
	}
	
	public boolean isExistentFile() {
		return isExistentFile;
	}
}
