package org.bladerunnerjs.utility.filemodification;

import java.io.File;

public class SecondaryFileModificationInfo implements FileModificationInfo {
	private final PrimaryFileModificationInfo primaryFileModificationInfo;
	private final FileModificationInfo fileModificationInfo;
	private final File file;
	
	public SecondaryFileModificationInfo(PrimaryFileModificationInfo primaryFileModificationInfo, File file, FileModificationInfo fileModificationInfo) {
		this.primaryFileModificationInfo = primaryFileModificationInfo;
		this.file = file;
		this.fileModificationInfo = fileModificationInfo;
	}
	
	@Override
	public long getLastModified() {
		return (file.isDirectory() && primaryFileModificationInfo.isFile()) ? 1 : fileModificationInfo.getLastModified();
	}
	
	@Override
	public void resetLastModified() {
		fileModificationInfo.resetLastModified();
	}
}
