package org.bladerunnerjs.memoization;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.util.Map;


public interface WatchService
{
	public WatchKey waitForEvents() throws InterruptedException;
	public Map<WatchKey,Path> createWatchKeysForDir(Path dirPath, boolean isNewlyDiscovered) throws IOException;
	public WatchKey createWatchKeyForDir(Path dirPath) throws IOException;
	public void close() throws IOException;
	
}
