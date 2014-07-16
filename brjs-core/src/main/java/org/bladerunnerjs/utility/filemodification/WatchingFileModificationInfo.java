package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.util.Set;

public interface WatchingFileModificationInfo extends FileModificationInfo {
	WatchingFileModificationInfo getParent();
	void addChild(WatchingFileModificationInfo childFileModificationInfo);
	Set<WatchingFileModificationInfo> getChildren();
	void setLastModified(long lastModified);
	File getFile();
	void pollWatchEvents();
	void closeWatchListener();
}
