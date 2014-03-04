package org.bladerunnerjs.spec.command;

import static org.bladerunnerjs.plugin.plugins.commands.standard.JsDocCommand.Messages.*;

import java.io.File;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.plugin.plugins.commands.standard.JsDocCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class IntegrationJsDocCommandTest extends SpecTest {
	App app;
	JsLib appLib;
	File jsdocOutputDir;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).hasCommands(new JsDocCommand())
			.and(brjs).hasBeenCreated();
		app = brjs.app("app");
		appLib = app.jsLib("lib");
		jsdocOutputDir = app.storageDir("jsdoc-toolkit");
	}
	
	@Test 
	public void runningJsDocCommandWithVerboseFlagCausesApiDocsToBeCreated() throws Exception {
		given(app).hasBeenCreated()
			.and(appLib).containsFileWithContents("src/MyClass.js", "/** @constructor */MyClass = function() {};");
		when(brjs).runCommand("jsdoc", "app", "-v");
		then(jsdocOutputDir).containsFile("MyClass.html")
			.and(output).containsLine(API_DOCS_GENERATED_MSG, jsdocOutputDir.getPath());
	}
	
	@Test 
	public void runningJsDocCommandCausesApiDocsToBeCreated() throws Exception {
		given(app).hasBeenCreated()
			.and(appLib).containsFileWithContents("src/MyClass.js", "/** @constructor */MyClass = function() {};");
		when(brjs).runCommand("jsdoc", "app");
		then(jsdocOutputDir).containsFile("MyClass.html")
			.and(output).containsLine(API_DOCS_GENERATED_MSG, jsdocOutputDir.getPath());
	}
}
