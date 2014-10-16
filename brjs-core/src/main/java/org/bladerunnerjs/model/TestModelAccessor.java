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
import org.bladerunnerjs.utility.filemodification.FileModificationService;
import org.bladerunnerjs.utility.filemodification.Java7FileModificationService;
import org.bladerunnerjs.utility.filemodification.SpecTestFileModificationService;
import org.bladerunnerjs.utility.filemodification.RealTimeAccessor;
import org.bladerunnerjs.utility.filemodification.TestTimeAccessor;


public class TestModelAccessor
{

	protected BRJS createModel(File brjsDir, PluginLocator pluginLocator, FileModificationService fileModificationService, LoggerFactory loggerFactory, AppVersionGenerator appVersionGenerator, FileModificationRegistry fileModificationRegistry) throws InvalidSdkDirectoryException
	{
		pluginLocator = (pluginLocator != null) ? pluginLocator : new MockPluginLocator();
		fileModificationService = (fileModificationService != null) ? fileModificationService : new SpecTestFileModificationService();
		loggerFactory = (loggerFactory != null) ? loggerFactory : new StubLoggerFactory();
		appVersionGenerator = (appVersionGenerator != null) ? appVersionGenerator : new MockAppVersionGenerator();		
		fileModificationRegistry = (fileModificationRegistry != null) ? fileModificationRegistry : new FileModificationRegistry(brjsDir);		
		
		BRJS brjs = new BRJS(brjsDir, pluginLocator, loggerFactory, new TestTimeAccessor(), appVersionGenerator, fileModificationRegistry);
		brjs.setFileModificationService(fileModificationService);
		
		return brjs;
	}

	protected BRJS createModel(File brjsDir, FileModificationService fileModificationService) throws InvalidSdkDirectoryException
	{
		return createModel(brjsDir, null, fileModificationService, null, null, null);
	}
	
	protected BRJS createModel(File brjsDir, PluginLocator pluginLocator, LogMessageStore logStore, AppVersionGenerator versionGenerator, FileModificationRegistry fileModificationRegistry) throws InvalidSdkDirectoryException 
	{	
		return createModel(brjsDir, pluginLocator, null, new TestLoggerFactory(logStore), versionGenerator, fileModificationRegistry);
	}
	
	protected BRJS createModel(File brjsDir, LoggerFactory loggerFactory) throws InvalidSdkDirectoryException
	{
		return createModel(brjsDir, null, null, loggerFactory, null, null);
	}
	
	protected BRJS createModel(File brjsDir, PluginLocator pluginLocator) throws InvalidSdkDirectoryException
	{
		return createModel(brjsDir, pluginLocator, null, null, null, null);
	}
	
	protected BRJS createModel(File brjsDir) throws InvalidSdkDirectoryException
	{
		return createModel(brjsDir, null, null, null, null, null);
	}
	
	
	public BRJS createNonTestModel(File brjsDir, LogMessageStore logStore, FileModificationRegistry fileModificationRegistry) throws InvalidSdkDirectoryException
	{
		LoggerFactory loggerFactory = new TestLoggerFactory(logStore);
		FileModificationService fileModificationService = new Java7FileModificationService(loggerFactory);
		return createNonTestModel(brjsDir, logStore, loggerFactory, fileModificationService, fileModificationRegistry);
	}
	
	public BRJS createNonTestModel(File brjsDir, LogMessageStore logStore, LoggerFactory loggerFactory, FileModificationService fileModificationService, FileModificationRegistry fileModificationRegistry) throws InvalidSdkDirectoryException
	{
		PluginLocator pluginLocator = new BRJSPluginLocator();
		AppVersionGenerator appVersionGenerator = new TimestampAppVersionGenerator();
		BRJS brjs = new BRJS(brjsDir, pluginLocator, loggerFactory, new RealTimeAccessor(), appVersionGenerator, fileModificationRegistry);
		brjs.setFileModificationService(fileModificationService);
		
		return brjs;
	}
	
}