package org.bladerunnerjs.spec.command;

import java.io.File;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.plugin.plugins.commands.standard.JsDocCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class JsDocCommandTest extends SpecTest {
	App app;
	JsLib appLib;
	File jsdocOutputDir;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).hasCommands(new JsDocCommand())
			.and(brjs).hasBeenCreated();
		app = brjs.app("app");
		appLib = app.jsLib("lib");
		jsdocOutputDir = app.storageDir("jsdoc");
	}
	
	// TODO: add this test once logging and console output have been merged into one thing, and we can use the same partial
	// line checking available for console output
	@Ignore
	@Test
	public void verboseFlagCausesAParseMessageForEachClass() throws Exception {
	}
	
	@Test
	public void runningJsDocForInvalidAppThrowsException() throws Exception {
		when(brjs).runCommand("jsdoc", "app");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted("App 'app' does not exist"));
	}
	
	@Test
	public void runningJsDocWithTooFewArgsThrowsEception() throws Exception {
		when(brjs).runCommand("jsdoc");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'app-name' is required"));
	}

	@Test
	public void runningJsDocWithTooManyArgsThrowsException() throws Exception {
		when(brjs).runCommand("jsdoc", "app", "extra");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: extra"));
	}
	
	@Test
	public void runningJsDocWithVerboseFlagAndTooManyArgsThrowsException() throws Exception {
		when(brjs).runCommand("jsdoc", "app", "extra");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: extra"));
	}
}
