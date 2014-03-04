package org.bladerunnerjs.utility.filemodification;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Date;
import java.util.List;

public class Java7FileModificationInfo implements FileModificationInfo {
	private final WatchKey watchKey;
	private final Java7FileModificationInfo parentModificationInfo;
	private long lastModified = (new Date()).getTime();
	
	public Java7FileModificationInfo(WatchService watchService, File dir, Java7FileModificationInfo parentModificationInfo) {
		try {
			this.parentModificationInfo = parentModificationInfo;
			watchKey = dir.toPath().register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public long getLastModified() {
		return lastModified;
	}
	
	public void doPoll() {
		List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
		
		if(watchEvents.size() > 0) {
			// TODO: we shouldn't update last-modified if the only changes to are hidden files
			updateLastModified();
			
			for(WatchEvent<?> watchEvent : watchEvents) {
				if(watchEvent.kind().type().equals(ENTRY_CREATE)) {
					// TODO: we need to process adds and removes properly, by creating more FileModificationInfo objects
					//       this will involve invoking initializeWatchers() for any new directories, and that method will need to be made thread-safe
				}
			}
		}
	}
	
	public void close() {
		watchKey.cancel();
	}
	
	private void updateLastModified() {
		lastModified = (new Date()).getTime();
		Java7FileModificationInfo nextModificationInfo = parentModificationInfo;
		
		while(nextModificationInfo != null) {
			nextModificationInfo.lastModified = lastModified;
			nextModificationInfo = nextModificationInfo.parentModificationInfo;
		}
	}
}
