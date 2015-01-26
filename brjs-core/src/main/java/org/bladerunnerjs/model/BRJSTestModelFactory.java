package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.LoggerFactory;
import org.bladerunnerjs.api.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.api.plugin.PluginLocator;
import org.bladerunnerjs.api.spec.utility.LogMessageStore;
import org.bladerunnerjs.api.spec.utility.MockAppVersionGenerator;
import org.bladerunnerjs.api.spec.utility.MockPluginLocator;
import org.bladerunnerjs.api.spec.utility.StubLoggerFactory;
import org.bladerunnerjs.api.spec.utility.TestLoggerFactory;
import org.bladerunnerjs.plugin.utility.BRJSPluginLocator;
import org.bladerunnerjs.utility.FileUtils;


public class BRJSTestModelFactory
{

	public static BRJS createModel(File brjsDir, PluginLocator pluginLocator, LoggerFactory loggerFactory, AppVersionGenerator appVersionGenerator) throws InvalidSdkDirectoryException
	{
		pluginLocator = (pluginLocator != null) ? pluginLocator : new MockPluginLocator();
		loggerFactory = (loggerFactory != null) ? loggerFactory : new StubLoggerFactory();
		appVersionGenerator = (appVersionGenerator != null) ? appVersionGenerator : new MockAppVersionGenerator();				
		
		BRJS brjs = new BRJS(brjsDir, pluginLocator, loggerFactory, appVersionGenerator);
		
		return brjs;
	}
	
	public static BRJS createModel(File brjsDir, LoggerFactory loggerFactory) throws InvalidSdkDirectoryException
	{
		return createModel(brjsDir, null, loggerFactory, null);
	}
	
	public static BRJS createModel(File brjsDir, PluginLocator pluginLocator) throws InvalidSdkDirectoryException
	{
		return createModel(brjsDir, pluginLocator, null, null);
	}
	
	public static BRJS createModel(File brjsDir) throws InvalidSdkDirectoryException
	{
		return createModel(brjsDir, null, null, null);
	}
	
	public static BRJS createNonTestModel(File brjsDir, LogMessageStore logStore) throws InvalidSdkDirectoryException
	{
		LoggerFactory loggerFactory = new TestLoggerFactory(logStore);
		return createNonTestModel(brjsDir, logStore, loggerFactory);
	}
	
	public static BRJS createNonTestModel(File brjsDir, LogMessageStore logStore, LoggerFactory loggerFactory) throws InvalidSdkDirectoryException
	{
		PluginLocator pluginLocator = new BRJSPluginLocator();
		AppVersionGenerator appVersionGenerator = new TimestampAppVersionGenerator();
		BRJS brjs = new BRJS(brjsDir, pluginLocator, loggerFactory, appVersionGenerator);
		
		return brjs;
	}
	
	public static File createTestSdkDirectory() {
		File sdkDir;
		
		try {
			sdkDir = FileUtils.createTemporaryDirectory( BRJSTestModelFactory.class );
			new File(sdkDir, "sdk").mkdirs();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return sdkDir;
	}
	
}