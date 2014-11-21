package org.bladerunnerjs.memoization;

import java.io.IOException;


public class WatchKeyServiceFactory
{

	public WatchKeyService createWatchService() throws IOException {
		if (WindowsFileTreeWatchKeyService.isSupported()) {
			return new WindowsFileTreeWatchKeyService();
		}
		if (MacHighSensitivityWatchKeyService.isSupported()) {
			return new MacHighSensitivityWatchKeyService();
		}
		return new DefaultWatchKeyService();
	}
	
}
