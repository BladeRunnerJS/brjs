package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.utility.filemodification.FileModificationInfo;

public class StandardFileInfo implements FileInfo {
	private final FileModificationInfo fileModificationInfo;
	
	public StandardFileInfo(File file, BRJS brjs, FileModificationInfo fileModificationInfo) {
		this.fileModificationInfo = fileModificationInfo;
	}
	
	@Override
	public long getLastModified() {
		return fileModificationInfo.getLastModified();
	}
	
	@Override
	public void resetLastModified() {
		fileModificationInfo.resetLastModified();
	}
}
