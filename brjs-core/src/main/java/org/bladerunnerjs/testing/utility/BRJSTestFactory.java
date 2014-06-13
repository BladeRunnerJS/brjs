package org.bladerunnerjs.testing.utility;

import java.io.File;

import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.logging.SLF4JLoggerFactory;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.plugin.PluginLocator;
import org.bladerunnerjs.utility.filemodification.PessimisticFileModificationService;

public class BRJSTestFactory {
	
	public static BRJS createBRJS(File brjsDir, PluginLocator pluginLocator, LoggerFactory loggerFactory) throws InvalidSdkDirectoryException {
		return new BRJS(brjsDir, pluginLocator, new PessimisticFileModificationService(), loggerFactory, new StubConsoleWriter(), new MockAppVersionGenerator());
	}
	
	public static BRJS createBRJS(File brjsDir, PluginLocator pluginLocator) throws InvalidSdkDirectoryException {
		return new BRJS(brjsDir, pluginLocator, new PessimisticFileModificationService(), new SLF4JLoggerFactory(), new StubConsoleWriter(), new MockAppVersionGenerator());
	}

	public static BRJS createBRJS(File brjsDir, LoggerFactory loggerFactory) throws InvalidSdkDirectoryException {
		return createBRJS(brjsDir, new MockPluginLocator(), loggerFactory);
	}
	
	public static BRJS createBRJS(File brjsDir) throws InvalidSdkDirectoryException {
		return createBRJS(brjsDir, new SLF4JLoggerFactory());
	}
}
