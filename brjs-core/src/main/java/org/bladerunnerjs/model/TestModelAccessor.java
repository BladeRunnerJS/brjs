package org.bladerunnerjs.model;

import java.io.File;
import java.io.PrintStream;

import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.console.PrintStreamConsoleWriter;
import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.logging.SLF4JLoggerFactory;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.plugin.PluginLocator;
import org.bladerunnerjs.plugin.utility.BRJSPluginLocator;
import org.bladerunnerjs.testing.specutility.engine.ConsoleMessageStore;
import org.bladerunnerjs.testing.specutility.engine.ConsoleStoreWriter;
import org.bladerunnerjs.testing.utility.LogMessageStore;
import org.bladerunnerjs.testing.utility.MockAppVersionGenerator;
import org.bladerunnerjs.testing.utility.MockPluginLocator;
import org.bladerunnerjs.testing.utility.StubConsoleWriter;
import org.bladerunnerjs.testing.utility.StubLoggerFactory;
import org.bladerunnerjs.testing.utility.TestLoggerFactory;
import org.bladerunnerjs.utility.filemodification.FileModificationService;
import org.bladerunnerjs.utility.filemodification.Java7FileModificationService;
import org.bladerunnerjs.utility.filemodification.PessimisticFileModificationService;


public class TestModelAccessor
{

	protected BRJS createModel(File brjsDir, PluginLocator pluginLocator, FileModificationService fileModificationService, LoggerFactory loggerFactory, ConsoleWriter consoleWriter, AppVersionGenerator appVersionGenerator) throws InvalidSdkDirectoryException
	{
		pluginLocator = (pluginLocator != null) ? pluginLocator : new MockPluginLocator();
		fileModificationService = (fileModificationService != null) ? fileModificationService : new PessimisticFileModificationService();
		loggerFactory = (loggerFactory != null) ? loggerFactory : new StubLoggerFactory();
		consoleWriter = (consoleWriter != null) ? consoleWriter : new StubConsoleWriter();
		appVersionGenerator = (appVersionGenerator != null) ? appVersionGenerator : new MockAppVersionGenerator();		
		
		return new BRJS(brjsDir, pluginLocator, fileModificationService, loggerFactory, consoleWriter, appVersionGenerator);
	}

	protected BRJS createModel(File brjsDir, PluginLocator pluginLocator, LogMessageStore logStore, ConsoleMessageStore consoleMessageStore, AppVersionGenerator versionGenerator) throws InvalidSdkDirectoryException 
	{	
		return createModel(brjsDir, pluginLocator, null, new TestLoggerFactory(logStore), new ConsoleStoreWriter(consoleMessageStore), versionGenerator);
	}
	
	protected BRJS createModel(File brjsDir, PrintStream printStream) throws InvalidSdkDirectoryException
	{
		return createModel(brjsDir, null, null, null, new PrintStreamConsoleWriter(printStream), null);
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
	
	
	public BRJS createNonTestModel(File brjsDir, LogMessageStore logStore, ConsoleMessageStore consoleWriterStrore) throws InvalidSdkDirectoryException
	{
		ConsoleWriter consoleWriter = new ConsoleStoreWriter(new ConsoleMessageStore());
		PluginLocator pluginLocator = new BRJSPluginLocator();
		LoggerFactory loggerFactory = new SLF4JLoggerFactory();
		AppVersionGenerator appVersionGenerator = new TimestampAppVersionGenerator();
		FileModificationService fileModificationService = new Java7FileModificationService(loggerFactory);
		
		return new BRJS(brjsDir, pluginLocator, fileModificationService, loggerFactory, consoleWriter, appVersionGenerator);
	}
	
}