package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.utility.filemodification.FileModifiedChecker;
import org.bladerunnerjs.utility.filemodification.InfoFileModifiedChecker;
import org.bladerunnerjs.utility.filemodification.FileModificationInfo;


public class SpecTestDirObserver
{
	private FileModifiedChecker fileModificationChecker;
	
	public FileModifiedChecker getDirObserver()
	{
		return fileModificationChecker;
	}
	
	public void setDirObserver(FileModificationInfo fileModificationInfo)
	{
		this.fileModificationChecker = new InfoFileModifiedChecker(fileModificationInfo);
	}
}
