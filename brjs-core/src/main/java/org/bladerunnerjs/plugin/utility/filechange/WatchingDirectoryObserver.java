package org.bladerunnerjs.plugin.utility.filechange;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;

public class WatchingDirectoryObserver implements DirectoryObserver {
	private final WatchService watcher;
	private boolean firstInvocation = true;
	
	public WatchingDirectoryObserver(File dir) {
		try {
			watcher = FileSystems.getDefault().newWatchService();
			dir.toPath().register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean hasChangedSinceLastCheck() {
		boolean hasChanged = !watcher.poll().pollEvents().isEmpty() || firstInvocation;
		firstInvocation = false;
		
		return hasChanged;
	}
	
	public void destroy() throws IOException {
		watcher.close();
	}
}
