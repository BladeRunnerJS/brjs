package org.bladerunnerjs.model;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

public class IO {
	private final Map<FileAccessLimitScope, File[]> activeScopes = new LinkedHashMap<>();
	private final SecurityManager securityManager;
	private final IOFileFilter jvmCoreFileFilter = new SuffixFileFilter( new String[] { ".class", ".jar", ".dat" } );
	
	public IO(IOFileFilter globalFileFilter) {
		securityManager = new BRJSSecurityManager( new OrFileFilter(jvmCoreFileFilter, globalFileFilter), activeScopes);
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
			Class.forName("org.bladerunnerjs.plugin.bundlers.aliasing.AliasesReader");
			Class.forName("org.bladerunnerjs.plugin.bundlers.aliasing.AliasDefinitionsReader");
		}
		catch (ClassNotFoundException e) {
			// do nothing since the classes might not be available
		}
	}
}
