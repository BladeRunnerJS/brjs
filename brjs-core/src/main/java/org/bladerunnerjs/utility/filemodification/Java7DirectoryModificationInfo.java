package org.bladerunnerjs.utility.filemodification;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.utility.RelativePathUtility;

public class Java7DirectoryModificationInfo implements WatchingFileModificationInfo {
	private final Java7FileModificationService fileModificationService;
	private final File dir;
	private final WatchKey watchKey;
	private final WatchingFileModificationInfo parentModificationInfo;
	private long lastModified = (new Date()).getTime();
	private final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
	private final String relativeDirPath;
	private Set<WatchingFileModificationInfo> children = new LinkedHashSet<>();
	private Logger logger;
	
	public Java7DirectoryModificationInfo(Java7FileModificationService fileModificationService, WatchService watchService, File dir, WatchingFileModificationInfo parentModificationInfo) {
		try {
			this.fileModificationService = fileModificationService;
			this.dir = dir;
			this.parentModificationInfo = parentModificationInfo;
			relativeDirPath = RelativePathUtility.get(fileModificationService.getRootDir(), dir);
			logger = fileModificationService.getLogger();
			
			if(parentModificationInfo != null) {
				parentModificationInfo.addChild(this);
			}
			
			watchKey = dir.toPath().register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void addChild(WatchingFileModificationInfo child) {
		children.add(child);
	}
	
	@Override
	public Set<WatchingFileModificationInfo> getChildren() {
		return children;
	}
	
	@Override
	public WatchingFileModificationInfo getParent() {
		return parentModificationInfo;
	}
	
	@Override
	public long getLastModified() {
		return lastModified;
	}
	
	@Override
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
		
		if(getParent() != null) {
			getParent().setLastModified(lastModified);
		}
	}
	
	@Override
	public void resetLastModified() {
		lastModified = (new Date()).getTime();;
	}
	
	@Override
	public File getFile() {
		return dir;
	}
	
	@Override
	public void pollWatchEvents() {
		List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
		long lastModified = new Date().getTime();
		boolean filesUpdated = false;
		
		for(WatchEvent<?> watchEvent : watchEvents) {
			Path path = (Path) watchEvent.context();
			File contextFile = new File(dir, path.toString());
			
			logger.debug("%s/%s (%s)", relativeDirPath, path, watchEvent.kind().name());
			
			if(!contextFile.isHidden()) {
				filesUpdated = true;
				
				ProxyFileModificationInfo proxyFileModificationInfo = fileModificationService.getModificationInfo(contextFile);
				
				if(watchEvent.kind().equals(ENTRY_CREATE)) {
					fileModificationService.watchDirectory(contextFile, this, lastModified);
				}
				else if(watchEvent.kind().equals(ENTRY_MODIFY)) {
					proxyFileModificationInfo.setLastModified(lastModified);
				}
				else if(watchEvent.kind().equals(ENTRY_DELETE)) {
					proxyFileModificationInfo.delete();
				}
			}
		}
		
		if(filesUpdated) {
			setLastModified(lastModified);
		}
	}
	
	// TODO: Waiting on Java bug fix http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8029516, github issue #385
	@Override
	public void closeWatchListener() {
		if(!isWindows) {
			watchKey.cancel();
		}
	}
}
