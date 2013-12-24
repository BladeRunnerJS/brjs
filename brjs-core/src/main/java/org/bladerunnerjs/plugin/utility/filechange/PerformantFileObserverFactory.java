package org.bladerunnerjs.plugin.utility.filechange;

import java.io.File;

public class PerformantFileObserverFactory implements FileObserverFactory {
	@Override
	public FileObserver createFileObserver(File file) {
		return new WatchingFileObserver(file);
	}
	
	@Override
	public DirectoryObserver createDirectoryObserver(File dir) {
		return new WatchingDirectoryObserver(dir);
	}
}
