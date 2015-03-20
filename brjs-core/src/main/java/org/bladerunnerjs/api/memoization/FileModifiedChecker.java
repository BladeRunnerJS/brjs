package org.bladerunnerjs.api.memoization;

import java.io.File;

import org.bladerunnerjs.model.engine.RootNode;


public class FileModifiedChecker
{
	private FileModificationRegistry fileModificationRegistry;
	private File file;
	private long lastFileVersion = -1;
	private FileVersion fileVersion;

	public FileModifiedChecker(FileModificationRegistry fileModificationRegistry, RootNode rootNode, File file) {
		this.fileModificationRegistry = fileModificationRegistry;
		this.file = file;
	}
	
	public boolean hasChangedSinceLastCheck() {
		if (fileVersion == null) {
			fileVersion = fileModificationRegistry.getFileVersionObject(file);
		}
		long newFileVersion = fileVersion.getValue();
		boolean hasChangedSinceLastCheck = (newFileVersion > lastFileVersion);
		lastFileVersion = newFileVersion;
		
		return hasChangedSinceLastCheck;
	}
	
}
