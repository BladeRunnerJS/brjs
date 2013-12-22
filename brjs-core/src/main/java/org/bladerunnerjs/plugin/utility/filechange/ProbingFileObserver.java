package org.bladerunnerjs.plugin.utility.filechange;

import java.io.File;

public class ProbingFileObserver implements FileObserver {
	private final File file;
	private Long previousModified = (long) -1;
	private Long previousFileSize = (long) -1;
	
	public ProbingFileObserver(File file) {
		this.file = file;
	}
	
	@Override
	public boolean hasChangedSinceLastCheck() {
		Long currentLastModified = file.lastModified();
		Long currentFileSize = file.length();
		
		boolean fileModified = !currentLastModified.equals(previousModified) || !currentFileSize.equals(previousFileSize);
		
		previousModified = currentLastModified;
		previousFileSize = currentFileSize;
		
		return fileModified;
	}
}
