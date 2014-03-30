package org.bladerunnerjs.model;

import java.io.File;
import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;
import java.util.Map;

public class BRJSSecurityManager extends SecurityManager {
	private final Map<FileAccessLimitScope, File[]> activeScopes;
	
	public BRJSSecurityManager(Map<FileAccessLimitScope, File[]> activeScopes) {
		this.activeScopes = activeScopes;
	}
	
	private void assertWithinScope(File file) throws BRJSMemoizationFileAccessException {
		if(!file.getName().endsWith(".class") && !file.getName().endsWith(".jar")) {
			for(File[] scopeFiles : activeScopes.values()) {
				boolean withinScope = false;
				
				for(File scopeFile : scopeFiles) {
					if(isAncestor(file, scopeFile)) {
						withinScope = true;
						break;
					}
				}
				
				if(!withinScope) {
					throw new BRJSMemoizationFileAccessException(file, scopeFiles);
				}
			}
		}
	}
	
	public static boolean isAncestor(File file, File ancestor) {
		File f = file;
		
		while (f != null) {
			if (f.equals(ancestor)) return true; f = f.getParentFile();
		}
		
		return false;
	}
	
	public void checkRead(String file) {
		assertWithinScope(new File(file));
	}
	
	public void checkRead(String file, Object context) {
		assertWithinScope(new File(file));
	}
	
	public void checkRead(FileDescriptor fd) {
		// do nothing
	}
	
	public void checkPermission(Permission perm) {
		// do nothing
	}
	
	public void checkPermission(Permission perm, Object context) {
		// do nothing
	}
	
	public void checkCreateClassLoader() {
		// do nothing
	}
	
	public void checkAccess(Thread t) {
		// do nothing
	}
	
	public void checkAccess(ThreadGroup g) {
		// do nothing
	}
	
	public void checkExit(int status) {
		// do nothing
	}
	
	public void checkExec(String cmd) {
		// do nothing
	}
	
	public void checkLink(String lib) {
		// do nothing
	}
	
	public void checkWrite(FileDescriptor fd) {
		// do nothing
	}
	
	public void checkWrite(String file) {
		// do nothing
	}
	
	public void checkDelete(String file) {
		// do nothing
	}
	
	public void checkConnect(String host, int port) {
		// do nothing
	}
	
	public void checkConnect(String host, int port, Object context) {
		// do nothing
	}
	
	public void checkListen(int port) {
		// do nothing
	}
	
	public void checkAccept(String host, int port) {
		// do nothing
	}
	
	public void checkMulticast(InetAddress maddr) {
		// do nothing
	}
	
	public void checkMulticast(InetAddress maddr, byte ttl) {
		// do nothing
	}
	
	public void checkPropertiesAccess() {
		// do nothing
	}
	
	public void checkPropertyAccess(String key) {
		// do nothing
	}
	
	public void checkPrintJobAccess() {
		// do nothing
	}
	
	public void checkSystemClipboardAccess() {
		// do nothing
	}
	
	public void checkAwtEventQueueAccess() {
		// do nothing
	}
	
	public void checkPackageAccess(String pkg) {
		// do nothing
	}
	
	public void checkPackageDefinition(String pkg) {
		// do nothing
	}
	
	public void checkSetFactory() {
		// do nothing
	}
	
	public void checkMemberAccess(Class<?> clazz, int which) {
		// do nothing
	}
	
	public void checkSecurityAccess(String target) {
		// do nothing
	}
}
