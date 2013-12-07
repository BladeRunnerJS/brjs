package com.caplin.cutlass.testing;

import java.io.File;
import java.io.PrintStream;

import org.bladerunnerjs.console.PrintStreamConsoleWriter;
import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.model.BRJS;


public class BRJSTestFactory {
	public static BRJS createBRJS(File brjsDir, LoggerFactory loggerFactory, PrintStream printStream) {
		return new BRJS(brjsDir, new CommandOnlyPluginLocator(), loggerFactory, new PrintStreamConsoleWriter(printStream));
	}
	
	public static BRJS createBRJS(File brjsDir, PrintStream printStream) {
		return new BRJS(brjsDir, new CommandOnlyPluginLocator(), new StubLoggerFactory(), new PrintStreamConsoleWriter(printStream));
	}
	
	public static BRJS createBRJS(File brjsDir) {
		return new BRJS(brjsDir, new CommandOnlyPluginLocator(), new StubLoggerFactory(), new StubConsoleWriter());
	}
}
