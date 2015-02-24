package org.bladerunnerjs.spec.command;

import static org.bladerunnerjs.model.engine.AbstractNode.Messages.*;
import static org.bladerunnerjs.plugin.commands.standard.CreateLibraryCommand.Messages.*;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.api.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.api.model.exception.name.InvalidDirectoryNameException;
import org.bladerunnerjs.api.model.exception.template.TemplateNotFoundException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.AppJsLib;
import org.bladerunnerjs.model.TemplateGroup;
import org.bladerunnerjs.plugin.commands.standard.CreateLibraryCommand;
import org.junit.Before;
import org.junit.Test;


public class CreateLibraryCommandTest extends SpecTest {
	App app;
	Aspect aspect;
	JsLib lib;
	JsLib badLib;
	TemplateGroup angularTemplates;
	TemplateGroup defaultTemplates;
	TemplateGroup myTemplateTemplates;
	StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommandPlugins(new CreateLibraryCommand())
			.and(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated()
			.and(brjs).usesProductionTemplates();
			app = brjs.app("app");
			aspect = app.aspect("default");
			lib = app.jsLib("lib");
			badLib = app.jsLib("lib#$@/");
			angularTemplates = brjs.sdkTemplateGroup("angular");
			defaultTemplates = brjs.sdkTemplateGroup("default");
			myTemplateTemplates = brjs.sdkTemplateGroup("myTemplate");
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooFewArguments() throws Exception {
		when(brjs).runCommand("create-library", "a");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'new-library-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception {
		when(brjs).runCommand("create-library", "a", "b", "c");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: c"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheLibraryNameIsNotAValidDirectoryName() throws Exception {
		given(app).hasBeenCreated()
			.and(logging).enabled();
		when(brjs).runCommand("create-library", "app", "lib#$@/");
		then(logging).errorMessageReceived(NODE_CREATION_FAILED_LOG_MSG, AppJsLib.class.getSimpleName(), badLib.dir().getPath())
			.and(exceptions).verifyException(InvalidDirectoryNameException.class, "lib#$@/", badLib.dir().getPath())
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAppDoesntExist() throws Exception {
		when(brjs).runCommand("create-library", "app", "lib");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted(app.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheLibAlreadyExists() throws Exception {
		given(app).hasBeenCreated()
			.and(lib).hasBeenCreated();
		when(brjs).runCommand("create-library", "app", "lib");
		then(exceptions).verifyException(NodeAlreadyExistsException.class, unquoted(lib.getClass().getSimpleName()));
	}
	
	@Test
	public void libIsCreatedWhenAllArgumentsAreValid() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("create-library", "app", "lib");
		then(lib).dirExists()
			.and(logging).containsFormattedConsoleMessage( LIBRARY_CREATE_SUCCESS_CONSOLE_MSG, "lib" )
			.and(logging).containsFormattedConsoleMessage( LIBRARY_PATH_CONSOLE_MSG, lib.dir() );
	}
	
	@Test
	public void theCorrectStructureIsCreatedWhenABRLibIsCreated() throws Exception {
		given(app).hasBeenCreated();
			//.and(defaultTemplates).templateGroupCreated();
		when(brjs).runCommand("create-library", "app", "lib");
		then(lib).dirExists()
			.and(lib).hasDir("src")
			.and(lib).hasDir("test-unit")
			.and(lib.dir()).containsFileWithContents("br-lib.conf", "requirePrefix: lib")
			.and(lib).doesNotHaveFile("thirdparty-lib.manifest");
	}
	
	@Test
	public void brLibCreatedUsingTheCommandCanBeUsedInTheBundle() throws Exception {
		given(app).hasBeenCreated()
			.and(aspect).indexPageHasContent("require('lib/someClass');");
		when(brjs).runCommand("create-library", "app", "lib")
			.and(lib).containsFileWithContents("src/lib/someClass.js", "lib class")
			.and(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("lib class");
	}
	
	@Test
	public void thirdpartyLibCanBeCreatedUsingASwitch() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("create-library", "app", "lib", "-t", "thirdparty");
		then( app.jsLib("lib") ).dirExists()
			.and(logging).containsFormattedConsoleMessage( LIBRARY_CREATE_SUCCESS_CONSOLE_MSG, "lib" )
			.and(logging).containsFormattedConsoleMessage( LIBRARY_PATH_CONSOLE_MSG, app.appJsLib("lib").dir() );
	}
	
	@Test
	public void thirdpartyLibCanBeCreatedUsingALonghandSwitch() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("create-library", "app", "lib", "--type", "thirdparty");
		then( app.jsLib("lib") ).dirExists()
			.and(logging).containsFormattedConsoleMessage( LIBRARY_CREATE_SUCCESS_CONSOLE_MSG, "lib" )
			.and(logging).containsFormattedConsoleMessage( LIBRARY_PATH_CONSOLE_MSG, app.jsLib("lib").dir() );
	}
	
	@Test
	public void theCorrectStructureIsCreatedWhenAThirdpartyLibIsCreated() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("create-library", "app", "lib", "-t", "thirdparty");
		then(lib).dirExists()
			.and(lib).doesNotHaveDir("src/lib")
			.and(lib).doesNotHaveDir("resources/lib")
			.and(lib).doesNotHaveDir("tests/test-unit/js-test-driver/")
			.and(lib.dir()).containsFileWithContents("thirdparty-lib.manifest", "css: \n"+"depends: \n"+"exports: window.lib\n"+"js: ")
			.and(lib).doesNotHaveFile("br-lib.conf");
	}
	
	@Test
	public void thirdpartyLibCreatedUsingTheCommandCanBeUsedInTheBundle() throws Exception {
		given(app).hasBeenCreated()
			.and(aspect).indexPageHasContent("require('lib');");
		when(brjs).runCommand("create-library", "app", "lib", "-t", "thirdparty")
			.and(lib).containsFileWithContents("someClass.js", "lib class")
			.and(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("lib class");
	}
	
	@Test
	public void anExceptionIsThrownIfTheLibraryTypeIsInvalid() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("create-library", "app", "lib", "-t", "INVALID");
		then(exceptions).verifyFormattedException( CommandArgumentsException.class, INVALID_LIB_TYPE_MESSAGE, "INVALID", unquoted("br, thirdparty") );
	}
	
	@Test
	public void brLibrariesCanBeCreatedInTheAppEvenIfTheyAreAlreadyInTheSdk() throws Exception {
		given(app).hasBeenCreated()
			.and(brjs.sdkLib("lib")).hasBeenCreated();
		when(brjs).runCommand("create-library", "app", "lib");
		then(app.appJsLib("lib")).dirExists();
	}
	
	@Test
	public void thirdpartyLibrariesCanBeCreatedInTheAppEvenIfTheyAreAlreadyInTheSdk() throws Exception {
		given(app).hasBeenCreated()
			.and(brjs.sdkLib("lib")).hasBeenCreated();
		when(brjs).runCommand("create-library", "app", "lib", "-t", "thirdparty");
		then(app.appJsLib("lib")).dirExists();
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(app).hasBeenCreated();
		when(brjs).runCommand("create-library", "app", "lib");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void appIsCreatedWithTheSpecifiedTemplate() throws Exception {
		given(brjs).hasBeenAuthenticallyCreated()
			.and(app).hasBeenCreated()
			.and(angularTemplates.template("brjsconformantjslibrootassetlocation")).containsFile("fileForLibAngular.txt");
		when(brjs).runCommand("create-library", "app", "lib", "--template", "angular");
		then(app.appJsLib("lib")).dirExists()
			.and(app.appJsLib("lib")).hasFile("fileForLibAngular.txt");
	}
	
	@Test
	public void appIsCreatedWithTheSpecifiedTemplateIfMoreTemplatesExist() throws Exception {
		given(brjs).hasBeenAuthenticallyCreated()
			.and(app).hasBeenCreated()
			.and(angularTemplates.template("brjsconformantjslibrootassetlocation")).containsFile("fileForLibAngular.txt")
			.and(defaultTemplates.template("brjsconformantjslibrootassetlocation")).containsFile("fileForLibDefault.txt")
			.and(myTemplateTemplates.template("brjsconformantjslibrootassetlocation")).containsFile("fileForLibMyTemplate.txt");
		when(brjs).runCommand("create-library", "app", "lib", "--template", "myTemplate");
		then(app.appJsLib("lib")).dirExists()
			.and(app.appJsLib("lib")).hasFile("fileForLibMyTemplate.txt");
	}
	
	@Test
	public void defaultTemplateIsUsedIfNoneSpecifiedAndMultipleTemplatesExist() throws Exception {
		given(brjs).hasBeenAuthenticallyCreated()
			.and(app).hasBeenCreated()
			.and(angularTemplates.template("brjsconformantjslibrootassetlocation")).containsFile("fileForLibAngular.txt")
			.and(defaultTemplates.template("brjsconformantjslibrootassetlocation")).containsFile("fileForLibDefault.txt")
			.and(myTemplateTemplates.template("brjsconformantjslibrootassetlocation")).containsFile("fileForLibMyTemplate.txt");
		when(brjs).runCommand("create-library", "app", "lib");
		then(app.appJsLib("lib")).dirExists()
			.and(app.appJsLib("lib")).hasFile("fileForLibDefault.txt");
	}
	
	@Test
	public void exceptionIsThrownIfSpecifiedTemplateDoesNotExist() throws Exception {
		given(brjs).hasBeenAuthenticallyCreated()
			.and(app).hasBeenCreated();
		when(brjs).runCommand("create-library", "app", "lib", "--template", "nonexistent");
		then(exceptions).verifyException(TemplateNotFoundException.class);
	}
}
