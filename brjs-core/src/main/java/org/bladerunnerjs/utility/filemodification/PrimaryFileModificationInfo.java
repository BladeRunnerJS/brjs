package org.bladerunnerjs.utility.filemodification;

import org.bladerunnerjs.model.FileInfo;

public class PrimaryFileModificationInfo implements FileModificationInfo {
	private final FileInfo fileInfo;
	private final FileModificationInfo fileModificationInfo;
	private boolean isFile;
	
	public PrimaryFileModificationInfo(FileInfo fileInfo, FileModificationInfo fileModificationInfo) {
		this.fileInfo = fileInfo;
		this.fileModificationInfo = fileModificationInfo;
	}
	
	@Override
	public long getLastModified() {
		isFile = !fileInfo.isDirectory();
		return fileModificationInfo.getLastModified();
	}
	
	@Override
	public void resetLastModified() {
		fileModificationInfo.resetLastModified();
	}
	
	public boolean isFile() {
		return isFile;
	}
}
