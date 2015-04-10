package org.bladerunnerjs.memoization;

import java.io.IOException;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.FileObserver;
import org.bladerunnerjs.api.memoization.WatchingFileModificationObserverThread;


public class WatchingFileModificationObserver implements FileObserver
{

	private WatchingFileModificationObserverThread thread;
	private BRJS brjs;
	
	public WatchingFileModificationObserver(BRJS brjs)
	{
		this.brjs = brjs;
	}

	@Override
	public void start() throws IOException
	{
		thread = new WatchingFileModificationObserverThread( brjs, new WatchKeyServiceFactory() );
		thread.start();
	}

	@Override
	public void stop() throws IOException, InterruptedException
	{
		thread.interrupt();
		thread.join();
	}

}
