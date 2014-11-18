package org.bladerunnerjs.memoization;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchKey;


public abstract class AbstractWatchService implements WatchService
{
 
	protected final java.nio.file.WatchService watchService;

	public AbstractWatchService() throws IOException
	{
		watchService = FileSystems.getDefault().newWatchService();
	}
	
	@Override
	public WatchKey waitForEvents() throws InterruptedException
	{
		return watchService.take();
	}
	
}
