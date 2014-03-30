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
	
	public void preventCompilerWarning() {
		// do nothing: some Java compilers insist that the local variable must be used, even though try-with-resources is useful for the auto-closing side effect; see:
		//   * <http://glenpeterson.blogspot.co.uk/2012/09/java-closures-and-start-end-problem.html>
		//   * <http://stackoverflow.com/questions/16588843/why-does-try-with-resource-require-a-local-variable>
	}
}
