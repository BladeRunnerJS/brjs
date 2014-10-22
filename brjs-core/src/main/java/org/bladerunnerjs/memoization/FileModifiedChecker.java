package org.bladerunnerjs.memoization;

import java.io.File;

import org.bladerunnerjs.model.engine.RootNode;


public class FileModifiedChecker
{
	private FileModificationRegistry fileModificationRegistry;
	private File file;
	private long lastModifiedTime = -1;

	public FileModifiedChecker(FileModificationRegistry fileModificationRegistry, RootNode rootNode, File file) {
		this.fileModificationRegistry = fileModificationRegistry;
		this.file = file;
	}
	
	public boolean hasChangedSinceLastCheck() {
		long newLastModified = fileModificationRegistry.getFileVersion(file);
		boolean hasChangedSinceLastCheck = (newLastModified > lastModifiedTime);
		lastModifiedTime = newLastModified;
		
		return hasChangedSinceLastCheck;
	}
	
}
