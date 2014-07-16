package org.bladerunnerjs.spec.command;

import static org.bladerunnerjs.plugin.plugins.commands.standard.JsDocCommand.Messages.*;

import java.io.File;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.SdkJsLib;
import org.bladerunnerjs.plugin.plugins.commands.standard.JsDocCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class IntegrationJsDocCommandTest extends SpecTest {
	App app;
	JsLib appLib;
	File jsdocOutputDir;
	private SdkJsLib sdkLib;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).hasCommandPlugins(new JsDocCommand())
			.and(brjs).hasBeenCreated()
			.and(brjs).usesProductionTemplates();
		app = brjs.app("app");
		appLib = app.jsLib("lib");
		jsdocOutputDir = app.storageDir("jsdoc");
		sdkLib = brjs.sdkLib("lib");
	}
	
	@Test 
	public void runningJsDocCommandCausesApiDocsToBeCreated() throws Exception {
		given(app).hasBeenCreated()
			.and(appLib).containsFileWithContents("src/MyClass.js", "/** Some JsDoc \n"+
					"@constructor \n"+
					"**/ \n"+
					"MyClass = function() {};")
			.and(sdkLib).containsFileWithContents("src/SomeClass.js", "/** Some JsDoc \n"+
					"@constructor \n"+
					"**/ \n"+
					"SomeClass = function() {};");
		when(brjs).runCommand("jsdoc", "app");
//		then(jsdocOutputDir).containsFile("MyClass.html") //TODO: work out why the jsdoc command cant find src files in spec tests
//			.and(jsdocOutputDir).containsFile("SomeClass.html")
		then(logging).containsFormattedConsoleMessage(API_DOCS_GENERATED_MSG, jsdocOutputDir.getPath());
	}
}
