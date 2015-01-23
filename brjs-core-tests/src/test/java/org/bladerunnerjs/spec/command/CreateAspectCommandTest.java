package org.bladerunnerjs.spec.command;

import static org.bladerunnerjs.model.engine.AbstractNode.Messages.*;
import static org.bladerunnerjs.plugin.plugins.commands.standard.CreateAspectCommand.Messages.*;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.model.TemplateGroup;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.model.exception.name.InvalidDirectoryNameException;
import org.bladerunnerjs.model.exception.template.TemplateNotFoundException;
import org.bladerunnerjs.plugin.plugins.commands.standard.CreateAspectCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class CreateAspectCommandTest extends SpecTest {
	App app;
	Aspect aspect;
	Aspect badAspect;
	TemplateGroup templates;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommandPlugins(new CreateAspectCommand())
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			aspect = app.aspect("aspect");
			badAspect = app.aspect("aspect#$@/");
			templates = brjs.sdkTemplateGroup("default");
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooFewArguments() throws Exception {
		when(brjs).runCommand("create-aspect", "a");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'new-aspect-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception {
		when(brjs).runCommand("create-aspect", "a", "b", "c");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: c"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAppDoesntExist() throws Exception {
		when(brjs).runCommand("create-aspect", "app", "aspect");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted(app.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAspectAlreadyExists() throws Exception {
		given(aspect).hasBeenCreated();
		when(brjs).runCommand("create-aspect", "app", "aspect");
		then(exceptions).verifyException(NodeAlreadyExistsException.class, unquoted(aspect.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAspectTemplateDoesNotExist() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("create-aspect", "app", "aspect");
		then(exceptions).verifyException(TemplateNotFoundException.class);
	}
	
	@Test
	public void exceptionIsThrownIfAspectNameIsInvalid() throws Exception {
		given(app).hasBeenCreated()
			.and(templates).templateGroupCreated()
			.and(logging).enabled();
		when(brjs).runCommand("create-aspect", "app", "aspect#$@/");
		then(logging).errorMessageReceived(NODE_CREATION_FAILED_LOG_MSG, "Aspect", badAspect.dir().getPath())
			.and(exceptions).verifyException(InvalidDirectoryNameException.class, "aspect#$@/", badAspect.dir().getPath())
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}

	@Test
	public void aspectIsCreatedWhenAllArgumentsAreValid() throws Exception {
		given(brjs.sdkTemplateGroup("default")).templateGroupCreated()
			.and(app).hasBeenCreated();
		when(brjs).runCommand("create-aspect", "app", "aspect");
		then(aspect).dirExists()
			.and(logging).containsFormattedConsoleMessage(ASPECT_CREATE_SUCCESS_CONSOLE_MSG, "aspect")
			.and(logging).containsFormattedConsoleMessage(ASPECT_PATH_CONSOLE_MSG, aspect.dir().getPath());
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs.sdkTemplateGroup("default")).templateGroupCreated()
			.and(brjs).hasBeenAuthenticallyCreated()
			.and(app).hasBeenCreated();
		when(brjs).runCommand("create-aspect", "app", "aspect");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
}
