package org.bladerunnerjs.memoization;

import java.io.IOException;


public class WatchServiceFactory
{

	public WatchService createWatchService() throws IOException {
		if (WindowsFileTreeWatchService.isSupported()) {
			return new WindowsFileTreeWatchService();
		}
		if (MacHighSensitivityWatchService.isSupported()) {
			return new MacHighSensitivityWatchService();
		}
		return new DefaultWatchService();
	}
	
}
