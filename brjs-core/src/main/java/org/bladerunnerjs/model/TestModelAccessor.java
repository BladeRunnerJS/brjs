package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.plugin.PluginLocator;
import org.bladerunnerjs.plugin.utility.BRJSPluginLocator;
import org.bladerunnerjs.testing.utility.LogMessageStore;
import org.bladerunnerjs.testing.utility.MockAppVersionGenerator;
import org.bladerunnerjs.testing.utility.MockPluginLocator;
import org.bladerunnerjs.testing.utility.StubLoggerFactory;
import org.bladerunnerjs.testing.utility.TestLoggerFactory;
import org.bladerunnerjs.utility.filemodification.FileModificationService;
import org.bladerunnerjs.utility.filemodification.Java7FileModificationService;
import org.bladerunnerjs.utility.filemodification.ReadWriteCompatiblePessimisticFileModificationService;


public class TestModelAccessor
{

	protected BRJS createModel(File brjsDir, PluginLocator pluginLocator, FileModificationService fileModificationService, LoggerFactory loggerFactory, AppVersionGenerator appVersionGenerator) throws InvalidSdkDirectoryException
	{
		pluginLocator = (pluginLocator != null) ? pluginLocator : new MockPluginLocator();
		fileModificationService = (fileModificationService != null) ? fileModificationService : new ReadWriteCompatiblePessimisticFileModificationService();
		loggerFactory = (loggerFactory != null) ? loggerFactory : new StubLoggerFactory();
		appVersionGenerator = (appVersionGenerator != null) ? appVersionGenerator : new MockAppVersionGenerator();		
		
		return new BRJS(brjsDir, pluginLocator, fileModificationService, loggerFactory, appVersionGenerator);
	}

	protected BRJS createModel(File brjsDir, PluginLocator pluginLocator, LogMessageStore logStore, AppVersionGenerator versionGenerator) throws InvalidSdkDirectoryException 
	{	
		return createModel(brjsDir, pluginLocator, null, new TestLoggerFactory(logStore), versionGenerator);
	}
	
	protected BRJS createModel(File brjsDir, LoggerFactory loggerFactory) throws InvalidSdkDirectoryException
	{
		return createModel(brjsDir, null, null, loggerFactory, null);
	}
	
	protected BRJS createModel(File brjsDir, PluginLocator pluginLocator) throws InvalidSdkDirectoryException
	{
		return createModel(brjsDir, pluginLocator, null, null, null);
	}
	
	protected BRJS createModel(File brjsDir) throws InvalidSdkDirectoryException
	{
		return createModel(brjsDir, null, null, null, null);
	}
	
	
	public BRJS createNonTestModel(File brjsDir, LogMessageStore logStore) throws InvalidSdkDirectoryException
	{
		LoggerFactory loggerFactory = new TestLoggerFactory(logStore);
		FileModificationService fileModificationService = new Java7FileModificationService(loggerFactory);
		return createNonTestModel(brjsDir, logStore, loggerFactory, fileModificationService);
	}
	
	public BRJS createNonTestModel(File brjsDir, LogMessageStore logStore, LoggerFactory loggerFactory, FileModificationService fileModificationService) throws InvalidSdkDirectoryException
	{
		PluginLocator pluginLocator = new BRJSPluginLocator();
		AppVersionGenerator appVersionGenerator = new TimestampAppVersionGenerator();
		
		return new BRJS(brjsDir, pluginLocator, fileModificationService, loggerFactory, appVersionGenerator);
	}
	
}