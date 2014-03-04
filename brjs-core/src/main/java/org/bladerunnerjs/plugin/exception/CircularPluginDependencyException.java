package org.bladerunnerjs.plugin.exception;

import org.bladerunnerjs.plugin.Plugin;

public class CircularPluginDependencyException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public CircularPluginDependencyException(Class<? extends Plugin> bundlerClass) {
		super("Circular dependency caused plugin '" + bundlerClass.getName() + "' to be accessed while it was still initializing:\n" + Thread.currentThread().getStackTrace());
	}
}

