package org.bladerunnerjs.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BRJSIO {
	private final Map<FileAccessLimitScope, File[]> activeScopes = new HashMap<>();
	private final SecurityManager securityManager = new BRJSSecurityManager(activeScopes);
	
	public FileAccessLimitScope limitAccessToWithin(File[] watchItems) {
		return new FileAccessLimitScope(activeScopes, watchItems);
	}
	
	public void installFileAccessChecker() {
//		System.setSecurityManager(securityManager);
	}
	
	public void uninstallFileAccessChecker() {
		System.setSecurityManager(null);
	}
}
