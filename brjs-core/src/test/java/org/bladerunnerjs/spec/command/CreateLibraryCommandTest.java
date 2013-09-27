package org.bladerunnerjs.spec.command;

import static org.bladerunnerjs.model.engine.AbstractNode.Messages.*;
import static org.bladerunnerjs.core.plugin.command.standard.CreateLibraryCommand.Messages.*;

import org.bladerunnerjs.core.plugin.command.standard.CreateLibraryCommand;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.model.exception.name.InvalidDirectoryNameException;
import org.bladerunnerjs.model.exception.name.InvalidPackageNameException;
import org.bladerunnerjs.model.exception.name.InvalidRootPackageNameException;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;



public class CreateLibraryCommandTest extends SpecTest {
	App app;
	JsLib lib;
	JsLib badLib;
	
	@Before
	public void initTestObjects() throws Exception
	{
		pluginLocator.pluginCommands.add( new CreateLibraryCommand() );
		given(brjs).hasBeenCreated();
		app = brjs.app("app");
		lib = app.jsLib("lib");
		badLib = app.jsLib("lib#$@/");
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooFewArguments() throws Exception {
		when(brjs).runCommand("create-library", "a", "b");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'library-namespace' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception {
		when(brjs).runCommand("create-library", "a", "b", "c", "d");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: d"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheLibraryNameIsNotAValidDirectoryName() throws Exception {
		given(app).hasBeenCreated()
			.and(logging).enabled();
		when(brjs).runCommand("create-library", "app", "lib#$@/", "libx");
		then(logging).errorMessageReceived(NODE_CREATION_FAILED_LOG_MSG, "JsLib", badLib.dir().getPath())
			.and(exceptions).verifyException(InvalidDirectoryNameException.class, "lib#$@/", badLib.dir().getPath())
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheLibraryNamespaceIsNotAValidPackageName() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("create-library", "app", "lib", "lib-x");
		then(exceptions).verifyException(InvalidPackageNameException.class, "lib-x", lib.dir().getPath())
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheLibraryNamespaceIsNotAValidRootPackageName() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("create-library", "app", "lib", "caplin");
		then(exceptions).verifyException(InvalidRootPackageNameException.class, "caplin", lib.dir().getPath())
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAppDoesntExist() throws Exception {
		when(brjs).runCommand("create-library", "app", "lib", "libx");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted(app.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheLibAlreadyExists() throws Exception {
		given(app).hasBeenCreated()
			.and(lib).hasBeenCreated();
		when(brjs).runCommand("create-library", "app", "lib", "libx");
		then(exceptions).verifyException(NodeAlreadyExistsException.class, unquoted(lib.getClass().getSimpleName()));
	}
	
	@Test
	public void libIsCreatedWhenAllArgumentsAreValid() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("create-library", "app", "lib", "libx");
		then(lib).dirExists()
			.and(output).containsLine( LIBRARY_CREATE_SUCCESS_CONSOLE_MSG, "lib" )
			.and(output).containsLine( LIBRARY_PATH_CONSOLE_MSG, lib.dir() );
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(app).hasBeenCreated();
		when(brjs).runCommand("create-library", "app", "lib", "libx");
		then(exceptions).verifyNoOutstandingExceptions();
	}
}
