package org.bladerunnerjs.memoization;

import java.io.File;


public class FileModifiedChecker
{
	private FileModificationRegistry fileModificationRegistry;
	private File file;
	private long lastModifiedTime = -1;

	public FileModifiedChecker(FileModificationRegistry fileModificationRegistry, File file) {
		this.fileModificationRegistry = fileModificationRegistry;
		this.file = file;
	}
	
	public boolean hasChangedSinceLastCheck() {
		long newLastModified = fileModificationRegistry.getLastModified(file);
		boolean hasChangedSinceLastCheck = (newLastModified > lastModifiedTime);
		lastModifiedTime = newLastModified;
		
		return hasChangedSinceLastCheck;
	}
	
}
