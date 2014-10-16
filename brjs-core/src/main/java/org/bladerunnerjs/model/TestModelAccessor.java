package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.memoization.FileModificationRegistry;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.plugin.PluginLocator;
import org.bladerunnerjs.plugin.utility.BRJSPluginLocator;
import org.bladerunnerjs.testing.utility.LogMessageStore;
import org.bladerunnerjs.testing.utility.MockAppVersionGenerator;
import org.bladerunnerjs.testing.utility.MockPluginLocator;
import org.bladerunnerjs.testing.utility.StubLoggerFactory;
import org.bladerunnerjs.testing.utility.TestLoggerFactory;


public class TestModelAccessor
{

	protected BRJS createModel(File brjsDir, PluginLocator pluginLocator, LoggerFactory loggerFactory, AppVersionGenerator appVersionGenerator, FileModificationRegistry fileModificationRegistry) throws InvalidSdkDirectoryException
	{
		pluginLocator = (pluginLocator != null) ? pluginLocator : new MockPluginLocator();
		loggerFactory = (loggerFactory != null) ? loggerFactory : new StubLoggerFactory();
		appVersionGenerator = (appVersionGenerator != null) ? appVersionGenerator : new MockAppVersionGenerator();		
		fileModificationRegistry = (fileModificationRegistry != null) ? fileModificationRegistry : new FileModificationRegistry(brjsDir.getParentFile());		
		
		BRJS brjs = new BRJS(brjsDir, pluginLocator, loggerFactory, appVersionGenerator, fileModificationRegistry);
		
		return brjs;
	}
	
	protected BRJS createModel(File brjsDir, LoggerFactory loggerFactory) throws InvalidSdkDirectoryException
	{
		return createModel(brjsDir, null, loggerFactory, null, null);
	}
	
	protected BRJS createModel(File brjsDir, PluginLocator pluginLocator) throws InvalidSdkDirectoryException
	{
		return createModel(brjsDir, pluginLocator, null, null, null);
	}
	
	protected BRJS createModel(File brjsDir) throws InvalidSdkDirectoryException
	{
		return createModel(brjsDir, null, null, null, null);
	}
	
	
	public BRJS createNonTestModel(File brjsDir, LogMessageStore logStore, FileModificationRegistry fileModificationRegistry) throws InvalidSdkDirectoryException
	{
		LoggerFactory loggerFactory = new TestLoggerFactory(logStore);
		return createNonTestModel(brjsDir, logStore, loggerFactory, fileModificationRegistry);
	}
	
	public BRJS createNonTestModel(File brjsDir, LogMessageStore logStore, LoggerFactory loggerFactory, FileModificationRegistry fileModificationRegistry) throws InvalidSdkDirectoryException
	{
		PluginLocator pluginLocator = new BRJSPluginLocator();
		AppVersionGenerator appVersionGenerator = new TimestampAppVersionGenerator();
		BRJS brjs = new BRJS(brjsDir, pluginLocator, loggerFactory, appVersionGenerator, fileModificationRegistry);
		
		return brjs;
	}
	
}