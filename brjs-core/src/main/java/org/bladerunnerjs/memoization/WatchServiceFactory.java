package org.bladerunnerjs.memoization;

import java.io.IOException;


public class WatchServiceFactory
{

	public WatchService createWatchService() throws IOException {
		return new DefaultWatchService();
	}
	
}
