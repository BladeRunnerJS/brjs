package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class Java7FileModificationInfo implements WatchingFileModificationInfo {
	private final WatchingFileModificationInfo parentModificationInfo;
	private final File file;
	long lastModified = 0;
	
	public Java7FileModificationInfo(WatchingFileModificationInfo parentModificationInfo, File file) {
		this.parentModificationInfo = parentModificationInfo;
		this.file = file;
		this.lastModified = parentModificationInfo.getLastModified();
		parentModificationInfo.addChild(this);
	}
	
	@Override
	public void addChild(WatchingFileModificationInfo childFileModificationInfo) {
		throw new RuntimeException("Java7FileModificationInfo.addChild() should never be invoked");
	}
	
	@Override
	public Set<WatchingFileModificationInfo> getChildren() {
		return new HashSet<>();
	}
	
	@Override
	public WatchingFileModificationInfo getParent() {
		return parentModificationInfo;
	}
	
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	
	@Override
	public long getLastModified() {
		return lastModified;
	}
	
	@Override
	public void resetLastModified() {
		lastModified = 0;
	}
	
	@Override
	public File getFile() {
		return file;
	}
	
	@Override
	public void pollWatchEvents() {
		// do nothing
	}
	
	@Override
	public void closeWatchListener() {
		// do nothing
	}
}
