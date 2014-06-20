package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.plugin.PluginLocator;
import org.bladerunnerjs.testing.specutility.engine.ConsoleMessageStore;
import org.bladerunnerjs.testing.specutility.engine.ConsoleStoreWriter;
import org.bladerunnerjs.testing.utility.LogMessageStore;
import org.bladerunnerjs.testing.utility.TestLoggerFactory;
import org.bladerunnerjs.utility.filemodification.PessimisticFileModificationService;


public class SpecTestModelAccessor
{
	protected BRJS createModel(File brjsDir, PluginLocator pluginLocator, LogMessageStore logStore, ConsoleMessageStore consoleMessageStore, AppVersionGenerator versionGenerator) throws InvalidSdkDirectoryException 
	{	
		return new BRJS(brjsDir, pluginLocator, new PessimisticFileModificationService(), new TestLoggerFactory(logStore), new ConsoleStoreWriter(consoleMessageStore), versionGenerator);
	}
	
	protected BRJS createNonTestModel(File brjsDir, LogMessageStore logStore, ConsoleMessageStore consoleMessageStore) throws InvalidSdkDirectoryException {
		return new BRJS(brjsDir, new TestLoggerFactory(logStore), new ConsoleStoreWriter(consoleMessageStore));
	}
}
