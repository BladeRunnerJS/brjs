package org.bladerunnerjs.model.utility;

import java.io.File;


public class FileModifiedChecker
{
	
	private File file;
	private Long lastModified = (long) -1;

	public FileModifiedChecker(File file)
	{
		this.file = file;
	}

	public boolean fileModifiedSinceLastCheck()
	{
		Long currentLastModified = file.lastModified();
		boolean fileModified = currentLastModified > lastModified;
		lastModified = currentLastModified;
		return fileModified;
	}

}
