package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.plugin.utility.filechange.DirectoryObserver;
import org.bladerunnerjs.plugin.utility.filechange.WatchingDirectoryObserver;


public class SpecTestDirObserver
{

	private DirectoryObserver dirObserver;

	public DirectoryObserver getDirObserver()
	{
		return dirObserver;
	}

	public void setDirObserver(WatchingDirectoryObserver dirObserver)
	{
		this.dirObserver = dirObserver;
	}

}
