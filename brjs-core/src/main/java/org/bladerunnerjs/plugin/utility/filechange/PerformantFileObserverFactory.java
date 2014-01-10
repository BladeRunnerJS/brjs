package org.bladerunnerjs.plugin.utility.filechange;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;

public class PerformantFileObserverFactory implements FileObserverFactory {
	private final WatchService watcher;
	
	{
		try {
			watcher = FileSystems.getDefault().newWatchService();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public FileObserver createFileObserver(File file) {
		// TODO: this is going to stop working as soon as we having more than one class set-up a watch on the same directory
		// -- we're going to need to pass an intermediary class instead
		return new WatchingFileObserver(watcher, file);
	}
	
	@Override
	public DirectoryObserver createDirectoryObserver(File dir) {
		// TODO: this is going to stop working as soon as we having more than one class set-up a watch on the same directory
		// -- we're going to need to pass an intermediary class instead
		return new WatchingDirectoryObserver(watcher, dir);
	}
	
	@Override
	public void close() {
		try {
			watcher.close();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
