package org.bladerunnerjs.plugin.utility.filechange;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class WatchingFileObserver implements FileObserver {
	private final WatchService watcher;
	private final String fileName;
	private boolean firstInvocation = true;
	
	public WatchingFileObserver(File file) {
		try {
			watcher = FileSystems.getDefault().newWatchService();
			fileName = file.getName();
			file.getParentFile().toPath().register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean hasChangedSinceLastCheck() {
		boolean hasChanged = fileEventsExists() || firstInvocation;
		firstInvocation = false;
		
		return hasChanged;
	}
	
	public void destroy() throws IOException {
		watcher.close();
	}
	
	private boolean fileEventsExists() {
		boolean fileEventExists = false;
		WatchKey watchKey = watcher.poll();
		
		if(watchKey != null) {
			for(WatchEvent<?> watchEvent : watchKey.pollEvents()) {
				@SuppressWarnings("unchecked")
				String eventFileName = ((WatchEvent<Path>) watchEvent).context().toFile().getName();
				
				if(eventFileName.equals(fileName)) {
					fileEventExists = true;
					break;
				}
			}
		}
		
		return fileEventExists;
	}
}
