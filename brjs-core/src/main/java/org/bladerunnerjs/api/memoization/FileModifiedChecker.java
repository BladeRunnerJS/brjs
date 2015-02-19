package org.bladerunnerjs.api.memoization;

import java.io.File;

import org.bladerunnerjs.model.engine.RootNode;


public class FileModifiedChecker
{
	private FileModificationRegistry fileModificationRegistry;
	private File file;
	private long lastModifiedTime = -1;
	private FileVersion fileVersion;

	public FileModifiedChecker(FileModificationRegistry fileModificationRegistry, RootNode rootNode, File file) {
		this.fileModificationRegistry = fileModificationRegistry;
		this.file = file;
	}
	
	public boolean hasChangedSinceLastCheck() {
		if (fileVersion == null) {
			fileVersion = fileModificationRegistry.getFileVersionObject(file);
		}
		long newLastModified = fileVersion.getValue();		
		
		boolean hasChangedSinceLastCheck = (newLastModified > lastModifiedTime);
		lastModifiedTime = newLastModified;
		
		return hasChangedSinceLastCheck;
	}
	
}
