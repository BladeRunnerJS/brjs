package org.bladerunnerjs.plugin.utility.filechange;

public class PessimisticDirectoryObserver implements DirectoryObserver {
	@Override
	public boolean hasChangedSinceLastCheck() {
		return true;
	}
	
	@Override
	public boolean hasRecursivelyChangedSinceLastCheck() {
		return true;
	}
	
	@Override
	public void reset() {
		// do nothing
	}
}
