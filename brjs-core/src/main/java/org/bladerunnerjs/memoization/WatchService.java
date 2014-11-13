package org.bladerunnerjs.memoization;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchKey;


public class WatchService
{

	private java.nio.file.WatchService watchService;

	public WatchService() throws IOException {
		watchService = FileSystems.getDefault().newWatchService();
	}
	
	public WatchKey createWatchKeyForDir(Path dirPath) throws IOException {
		return dirPath.register(watchService,ENTRY_CREATE,ENTRY_DELETE,ENTRY_MODIFY);
	}
	
	public void close() throws IOException {
		watchService.close();
	}
	
}
