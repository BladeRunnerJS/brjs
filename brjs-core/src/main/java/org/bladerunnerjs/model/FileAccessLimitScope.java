package org.bladerunnerjs.model;

import java.io.File;
import java.util.Map;

public class FileAccessLimitScope implements AutoCloseable {
	private final Map<FileAccessLimitScope, File[]> activeScopes;
	private final String scopeIdentifier;
	
	public FileAccessLimitScope(String scopeIdentifier, Map<FileAccessLimitScope, File[]> activeScopes, File[] watchItems) {
		this.scopeIdentifier = scopeIdentifier;
		this.activeScopes = activeScopes;
		activeScopes.put(this, watchItems);
	}
	
	@Override
	public void close() {
		activeScopes.remove(this);
	}
	
	public String getScopeIdentifier() {
		return scopeIdentifier;
	}
	
}
