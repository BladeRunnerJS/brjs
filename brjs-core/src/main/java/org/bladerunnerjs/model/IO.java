package org.bladerunnerjs.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

public class IO {
	private final Map<FileAccessLimitScope, File[]> activeScopes = new HashMap<>();
	private final SecurityManager securityManager;
	private final IOFileFilter classFileAndJarFileFilter = new SuffixFileFilter( new String[] { ".class", ".jar" } );
	
	public IO(IOFileFilter globalFileFilter) {
		securityManager = new BRJSSecurityManager( new OrFileFilter(classFileAndJarFileFilter, globalFileFilter), activeScopes);
	}
	
	public FileAccessLimitScope limitAccessToWithin(String scopeIdentifier, File[] watchItems) {
		return new FileAccessLimitScope(scopeIdentifier, activeScopes, watchItems);
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
