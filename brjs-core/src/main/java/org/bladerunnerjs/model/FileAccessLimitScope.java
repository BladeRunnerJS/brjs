package org.bladerunnerjs.model;

import java.io.File;
import java.util.Map;

public class FileAccessLimitScope implements AutoCloseable {
	private final Map<FileAccessLimitScope, File[]> activeScopes;
	
	public FileAccessLimitScope(Map<FileAccessLimitScope, File[]> activeScopes, File[] watchItems) {
		this.activeScopes = activeScopes;
		activeScopes.put(this, watchItems);
	}
	
	@Override
	public void close() {
		activeScopes.remove(this);
	}
}
