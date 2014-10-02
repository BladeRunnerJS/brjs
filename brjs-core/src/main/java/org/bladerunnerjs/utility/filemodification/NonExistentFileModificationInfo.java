package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class NonExistentFileModificationInfo implements WatchingFileModificationInfo {
	long lastModified = 0;
	private final WatchingFileModificationInfo parent;
	private final TimeAccessor timeAccessor;
	
	public NonExistentFileModificationInfo(WatchingFileModificationInfo parent, TimeAccessor timeAccessor) {
		this.parent = parent;
		this.timeAccessor = timeAccessor;
	}
	
	@Override
	public WatchingFileModificationInfo getParent() {
		return parent;
	}
	
	@Override
	public void addChild(WatchingFileModificationInfo childFileModificationInfo) {
		// do nothing
	}
	
	@Override
	public Set<WatchingFileModificationInfo> getChildren() {
		return new HashSet<>();
	}
	
	@Override
	public long getLastModified() {
		return lastModified;
	}
	
	@Override
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
		
		if(parent != null) {
			parent.setLastModified(lastModified);
		}
	}
	
	@Override
	public void resetLastModified() {
		setLastModified(timeAccessor.getTime());
	}
	
	@Override
	public File getFile() {
		return null;
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
