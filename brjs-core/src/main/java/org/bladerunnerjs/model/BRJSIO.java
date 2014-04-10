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
		executeFileAccessingStaticInitializers();
		System.setSecurityManager(securityManager);
	}
	
	public void uninstallFileAccessChecker() {
		System.setSecurityManager(null);
	}
	
	private void executeFileAccessingStaticInitializers() {
		try {
			Class.forName("java.util.Currency");
			Class.forName("org.bladerunnerjs.aliasing.aliases.AliasesReader");
			Class.forName("org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsReader");
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
