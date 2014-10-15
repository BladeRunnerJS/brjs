package org.bladerunnerjs.memoization;

import java.io.File;

import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.FileUtility;


public class FileModifiedChecker
{
	private FileModificationRegistry fileModificationRegistry;
	private File file;
	private long lastModifiedTime = -1;

	public FileModifiedChecker(FileModificationRegistry fileModificationRegistry, RootNode rootNode, File file) {
		this.fileModificationRegistry = fileModificationRegistry;
		file = FileUtility.getCanonicalFileWhenPossible(file);
		this.file = file;
	}
	
	public boolean hasChangedSinceLastCheck() {
		long newLastModified = fileModificationRegistry.getLastModified(file);
		boolean hasChangedSinceLastCheck = (newLastModified > lastModifiedTime);
		lastModifiedTime = newLastModified;
		
		return hasChangedSinceLastCheck;
	}
	
}
