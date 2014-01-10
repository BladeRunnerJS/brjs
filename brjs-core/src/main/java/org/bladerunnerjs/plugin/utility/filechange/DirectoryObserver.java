package org.bladerunnerjs.plugin.utility.filechange;

public interface DirectoryObserver {
	boolean hasChangedSinceLastCheck();
	void reset();
}
