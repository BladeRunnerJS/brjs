package org.bladerunnerjs.utility;

import java.io.File;


public class FileModifiedChecker
{
	
	private File file;
	private Long previousModified = (long) -1;
	private Long previousFileSize = (long) -1;
	
	public FileModifiedChecker(File file)
	{
		this.file = file;
	}

	public boolean fileModifiedSinceLastCheck()
	{
		Long currentLastModified = file.lastModified();
		Long currentFileSize = file.length();
		
		boolean fileModified = !currentLastModified.equals(previousModified) || !currentFileSize.equals(previousFileSize);
		
		previousModified = currentLastModified;
		previousFileSize = currentFileSize;
		
		return fileModified;
	}

}
