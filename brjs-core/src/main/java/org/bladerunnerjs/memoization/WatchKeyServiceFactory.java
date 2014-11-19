package org.bladerunnerjs.memoization;

import java.io.IOException;


public class WatchKeyServiceFactory
{

	public WatchKeyService createWatchService() throws IOException {
		if (FileTreeWatchKeyService.isSupported()) {
			return new FileTreeWatchKeyService();
		}
		if (HighSensitivityWatchKeyService.isSupported()) {
			return new HighSensitivityWatchKeyService();
		}
		return new DefaultWatchKeyService();
	}
	
}
