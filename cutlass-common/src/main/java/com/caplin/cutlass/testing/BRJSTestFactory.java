package com.caplin.cutlass.testing;

import java.io.File;
import java.io.PrintStream;

import org.bladerunnerjs.console.PrintStreamConsoleWriter;
import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.testing.utility.MockAppVersionGenerator;
import org.bladerunnerjs.utility.filemodification.PessimisticFileModificationService;


public class BRJSTestFactory {
	public static BRJS createBRJS(File brjsDir, LoggerFactory loggerFactory, PrintStream printStream) throws InvalidSdkDirectoryException {
		return new BRJS(brjsDir, new CommandOnlyPluginLocator(), new PessimisticFileModificationService(), loggerFactory, new PrintStreamConsoleWriter(printStream), new MockAppVersionGenerator());
	}
	
	public static BRJS createBRJS(File brjsDir, PrintStream printStream) throws InvalidSdkDirectoryException {
		return new BRJS(brjsDir, new CommandOnlyPluginLocator(), new PessimisticFileModificationService(), new StubLoggerFactory(), new PrintStreamConsoleWriter(printStream), new MockAppVersionGenerator());
	}
	
	public static BRJS createBRJS(File brjsDir) throws InvalidSdkDirectoryException {
		return new BRJS(brjsDir, new CommandOnlyPluginLocator(), new PessimisticFileModificationService(), new StubLoggerFactory(), new StubConsoleWriter(), new MockAppVersionGenerator());
	}
}
