package org.bladerunnerjs.testing.utility;

import java.io.File;

import org.bladerunnerjs.core.log.LoggerFactory;
import org.bladerunnerjs.core.log.SLF4JLoggerFactory;
import org.bladerunnerjs.core.plugin.PluginLocator;
import org.bladerunnerjs.model.BRJS;

public class BRJSTestFactory {
	
	public static BRJS createBRJS(File brjsDir, PluginLocator pluginLocator, LoggerFactory loggerFactory) {
		return new BRJS(brjsDir, pluginLocator, loggerFactory, new StubConsoleWriter());
	}
	
	public static BRJS createBRJS(File brjsDir, PluginLocator pluginLocator) {
		return new BRJS(brjsDir, pluginLocator, new SLF4JLoggerFactory(), new StubConsoleWriter());
	}

	public static BRJS createBRJS(File brjsDir, LoggerFactory loggerFactory) {
		return createBRJS(brjsDir, new MockPluginLocator(), loggerFactory);
	}
	
	public static BRJS createBRJS(File brjsDir) {
		return createBRJS(brjsDir, new SLF4JLoggerFactory());
	}
}
