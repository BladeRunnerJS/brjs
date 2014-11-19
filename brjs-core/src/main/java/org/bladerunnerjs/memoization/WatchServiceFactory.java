package org.bladerunnerjs.memoization;

import java.io.IOException;


public class WatchServiceFactory
{

	public WatchService createWatchService() throws IOException {
		if (ExtendedWatchEventModifierWatchService.isSupported()) {
			return new ExtendedWatchEventModifierWatchService();
		}
		if (SensitivityWatchEventModifierWatchService.isSupported()) {
			return new SensitivityWatchEventModifierWatchService();
		}
		return new DefaultWatchService();
	}
	
}
