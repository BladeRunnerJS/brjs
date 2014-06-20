package org.bladerunnerjs.model;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.console.PrintStreamConsoleWriter;
import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.logging.SLF4JLoggerFactory;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.plugin.PluginLocator;
import org.bladerunnerjs.plugin.utility.BRJSPluginLocator;
import org.bladerunnerjs.utility.filemodification.FileModificationService;
import org.bladerunnerjs.utility.filemodification.Java7FileModificationService;

/**
 * A utility class so Servlets and Filters can share a single BRJS model instance.
 * 
 * WARNING: Do not use this class. Any plugins that should have a reference to the BRJS instance will be provided it in the setBRJS() method. 
 *
 */
// Note: this should be the only static state within 'brjs-core'
public class BRJSModelAccessor {
	private static BRJS model;
	private static final ReentrantLock lock = new ReentrantLock();
	
	//TODO: remove this once we've remove all legacy code
	public static BRJS root;
	
	public static synchronized BRJS initializeModel(File brjsDir) throws InvalidSdkDirectoryException {
		ConsoleWriter consoleWriter = new PrintStreamConsoleWriter(System.out);
		PluginLocator pluginLocator = new BRJSPluginLocator();
		LoggerFactory loggerFactory = new SLF4JLoggerFactory();
		AppVersionGenerator versionGenerator = new TimestampAppVersionGenerator();
		FileModificationService fileModificationService = new Java7FileModificationService(loggerFactory);
		return initializeModel(brjsDir, pluginLocator, fileModificationService, loggerFactory, consoleWriter, versionGenerator); 
	}
	
	public static synchronized BRJS initializeModel(File brjsDir, FileModificationService fileModificationService) throws InvalidSdkDirectoryException {
		ConsoleWriter consoleWriter = new PrintStreamConsoleWriter(System.out);
		PluginLocator pluginLocator = new BRJSPluginLocator();
		LoggerFactory loggerFactory = new SLF4JLoggerFactory();
		AppVersionGenerator versionGenerator = new TimestampAppVersionGenerator();
		return initializeModel(brjsDir, pluginLocator, fileModificationService, loggerFactory, consoleWriter, versionGenerator); 
	}
	
	public static synchronized BRJS initializeModel(File brjsDir, PluginLocator pluginLocator, FileModificationService fileModificationService, 
				LoggerFactory loggerFactory, ConsoleWriter consoleWriter, AppVersionGenerator versionGenerator) throws InvalidSdkDirectoryException {
		if (model == null) {
			model = new BRJS(brjsDir, pluginLocator, fileModificationService, loggerFactory, consoleWriter, versionGenerator);
			root = model;
		}
		return model;
	}
	
	//TODO: this should be removed once all legacy code is removed
	public static synchronized BRJS initializeModel(BRJS brjs) throws InvalidSdkDirectoryException {
	if (model == null) {
		model = brjs;
		root = model;
	}
	return model;
}
	
	public static synchronized void destroy() {
		if(model != null) {
			try {
				model.close();
			}
			finally {
				model = null;
			}
		}
	}
	
	public static BRJS aquireModel() {
		lock.lock();
		return model;
	}
	
	public static void releaseModel() {
		lock.unlock();
	}
}