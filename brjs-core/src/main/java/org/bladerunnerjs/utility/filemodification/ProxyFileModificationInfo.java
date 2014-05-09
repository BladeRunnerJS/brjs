package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.util.Set;

public class ProxyFileModificationInfo implements WatchingFileModificationInfo {
	private final Java7FileModificationService fileModificationService;
	private NonExistentFileModificationInfo nonExistentFileModificationInfo = new NonExistentFileModificationInfo();
	private WatchingFileModificationInfo existentFileModificationInfo = null;
	
	public ProxyFileModificationInfo(Java7FileModificationService fileModificationService) {
		this.fileModificationService = fileModificationService;
	}
	
	public void setFileModificationInfo(WatchingFileModificationInfo fileModificationInfo) {
		existentFileModificationInfo = fileModificationInfo;
	}
	
	@Override
	public WatchingFileModificationInfo getParent() {
		return getActiveFMI().getParent();
	}
	
	@Override
	public void addChild(WatchingFileModificationInfo childFileModificationInfo) {
		getActiveFMI().addChild(childFileModificationInfo);
	}
	
	@Override
	public Set<WatchingFileModificationInfo> getChildren() {
		return getActiveFMI().getChildren();
	}
	
	@Override
	public long getLastModified() {
		return getActiveFMI().getLastModified();
	}
	
	@Override
	public void resetLastModified() {
		getActiveFMI().resetLastModified();
	}
	
	@Override
	public void setLastModified(long lastModified) {
		getActiveFMI().setLastModified(lastModified);
	}
	
	@Override
	public File getFile() {
		return getActiveFMI().getFile();
	}
	
	@Override
	public void pollWatchEvents() {
		getActiveFMI().pollWatchEvents();
	}
	
	@Override
	public void closeWatchListener() {
		getActiveFMI().closeWatchListener();
	}
	
	public void delete() {
		if(existentFileModificationInfo != null) {
			for(WatchingFileModificationInfo childInfo : getChildren()) {
				fileModificationService.getModificationInfo(childInfo.getFile()).delete();
			}
			
			nonExistentFileModificationInfo.setLastModified(existentFileModificationInfo.getLastModified() + 1);
			existentFileModificationInfo = null;
		}
	}
	
	private WatchingFileModificationInfo getActiveFMI() {
		return (existentFileModificationInfo != null) ? existentFileModificationInfo : nonExistentFileModificationInfo;
	}
}
