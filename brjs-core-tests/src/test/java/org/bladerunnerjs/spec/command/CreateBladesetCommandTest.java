package org.bladerunnerjs.spec.command;

import static org.bladerunnerjs.model.engine.AbstractNode.Messages.*;
import static org.bladerunnerjs.plugin.plugins.commands.standard.CreateBladesetCommand.Messages.*;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.model.exception.name.InvalidDirectoryNameException;
import org.bladerunnerjs.plugin.plugins.commands.standard.CreateBladesetCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class CreateBladesetCommandTest extends SpecTest {
	App app;
	Bladeset bladeset;
	Bladeset badBladeset;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommandPlugins(new CreateBladesetCommand())
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			bladeset = app.bladeset("bladeset");
			badBladeset = app.bladeset("bladeset#$@/");
	}
	
	
	@Test
	public void exceptionIsThrownIfThereAreTooFewArguments() throws Exception {
		when(brjs).runCommand("create-bladeset", "a");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'new-bladeset-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception {
		when(brjs).runCommand("create-bladeset", "a", "b", "c");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: c"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAppDoesntExist() throws Exception {
		when(brjs).runCommand("create-bladeset", "app", "bladeset");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted(app.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheBladesetAlreadyExists() throws Exception {
		given(bladeset).hasBeenCreated();
		when(brjs).runCommand("create-bladeset", "app", "bladeset");
		then(exceptions).verifyException(NodeAlreadyExistsException.class, unquoted(bladeset.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfBladesetNameIsInvalid() throws Exception {
		given(bladeset).hasBeenCreated()
			.and(logging).enabled();
		when(brjs).runCommand("create-bladeset", "app", "bladeset#$@/");
		then(logging).errorMessageReceived(NODE_CREATION_FAILED_LOG_MSG, "Bladeset", badBladeset.dir().getPath())
			.and(exceptions).verifyException(InvalidDirectoryNameException.class, "bladeset#$@/", badBladeset.dir().getPath())
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void bladeIsCreatedWhenAllArgumentsAreValid() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("create-bladeset", "app", "bladeset");
		then(bladeset).dirExists()
			.and(logging).containsFormattedConsoleMessage(BLADESET_CREATE_SUCCESS_CONSOLE_MSG, "bladeset")
			.and(logging).containsFormattedConsoleMessage(BLADESET_PATH_CONSOLE_MSG, bladeset.dir().getPath());
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(app).hasBeenCreated();
		when(brjs).runCommand("create-bladeset", "app", "bladeset");
		then(exceptions).verifyNoOutstandingExceptions();
	}
}
