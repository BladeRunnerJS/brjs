package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.utility.filemodification.FileModificationService;
import org.bladerunnerjs.utility.filemodification.FileModifiedChecker;
import org.bladerunnerjs.utility.filemodification.InfoFileModifiedChecker;
import org.bladerunnerjs.utility.filemodification.FileModificationInfo;
import org.bladerunnerjs.utility.filemodification.Java7FileModificationService;


public class SpecTestDirObserver
{
	private final Java7FileModificationService fileModificationService = new Java7FileModificationService(new StubLoggerFactory());
	private FileModifiedChecker fileModificationChecker;
	
	public FileModifiedChecker getDirObserver()
	{
		return fileModificationChecker;
	}
	
	public void setDirObserver(FileModificationInfo fileModificationInfo)
	{
		this.fileModificationChecker = new InfoFileModifiedChecker(fileModificationInfo);
	}

	public FileModificationService getFileModificationService() {
		return fileModificationService;
	}
}
