package org.bladerunnerjs.plugin.utility.filechange;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.WatchService;

public class WatchingDirectoryObserver implements DirectoryObserver {
	private final WatchService watcher;
	private boolean firstInvocation = true;
	
	public WatchingDirectoryObserver(WatchService watcher, File dir) {
		try {
			this.watcher = watcher;
			dir.toPath().register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean hasChangedSinceLastCheck() {
		boolean hasChanged = (watcher.poll() != null) || firstInvocation;
		firstInvocation = false;
		
		return hasChanged;
	}
	
	@Override
	public boolean hasRecursivelyChangedSinceLastCheck() {
		// TODO: we need to update the watch to inform parents when there has been a change
		return hasChangedSinceLastCheck();
	}
	
	@Override
	public void reset() {
		firstInvocation = true;
	}
}
