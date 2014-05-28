package org.bladerunnerjs.spec.command;

import static org.bladerunnerjs.model.engine.AbstractNode.Messages.*;
import static org.bladerunnerjs.plugin.plugins.commands.standard.CreateBladeCommand.Messages.*;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.model.exception.name.InvalidDirectoryNameException;
import org.bladerunnerjs.plugin.plugins.commands.standard.CreateBladeCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class CreateBladeCommandTest extends SpecTest {
	App app;
	Bladeset bladeset;
	Blade blade;
	Blade badBlade;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommands(new CreateBladeCommand())
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			bladeset = app.bladeset("bladeset");
			blade = bladeset.blade("blade");
			badBlade = bladeset.blade("!$%$^");
	}
	
	
	@Test
	public void exceptionIsThrownIfThereAreTooFewArguments() throws Exception {
		when(brjs).runCommand("create-blade", "a", "b");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'new-blade-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception {
		when(brjs).runCommand("create-blade", "a", "b", "c", "d");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: d"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAppDoesntExist() throws Exception {
		when(brjs).runCommand("create-blade", "app", "bladeset", "blade");
		then(exceptions).verifyException(NodeDoesNotExistException.class, "app", unquoted(app.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheBladesetDoesntExist() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("create-blade", "app", "bladeset", "blade");
		then(exceptions).verifyException(NodeDoesNotExistException.class, "bladeset", unquoted(bladeset.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheBladeAlreadyExists() throws Exception {
		given(blade).hasBeenCreated();
		when(brjs).runCommand("create-blade", "app", "bladeset", "blade");
		then(exceptions).verifyException(NodeAlreadyExistsException.class, "blade", unquoted(blade.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfBladeNameIsInvalid() throws Exception {
		given(bladeset).hasBeenCreated()
			.and(logging).enabled();
		when(brjs).runCommand("create-blade", "app", "bladeset", "!$%$^");
		then(logging).errorMessageReceived(NODE_CREATION_FAILED_LOG_MSG, "Blade", badBlade.dir().getPath())
			.and(exceptions).verifyException(InvalidDirectoryNameException.class, "!$%$^", badBlade.dir().getPath())
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void bladeIsCreatedWhenAllArgumentsAreValid() throws Exception {
		given(bladeset).hasBeenCreated();
		when(brjs).runCommand("create-blade", "app", "bladeset", "blade");
		then(blade).dirExists()
			.and(output).containsLine(BLADE_CREATE_SUCCESS_CONSOLE_MSG, "blade")
			.and(output).containsLine(BLADE_PATH_CONSOLE_MSG, blade.dir().getPath());
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(bladeset).hasBeenCreated();
		when(brjs).runCommand("create-blade", "app", "bladeset", "blade");
		then(exceptions).verifyNoOutstandingExceptions();
	}
}
