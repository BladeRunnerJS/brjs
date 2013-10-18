package org.bladerunnerjs.spec.command;

import static org.bladerunnerjs.model.App.Messages.*;
import static org.bladerunnerjs.model.engine.AbstractNode.Messages.*;
import static org.bladerunnerjs.core.plugin.command.standard.CreateApplicationCommand.Messages.*;

import org.bladerunnerjs.core.plugin.command.standard.CreateApplicationCommand;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.model.exception.name.InvalidDirectoryNameException;
import org.bladerunnerjs.model.exception.name.InvalidPackageNameException;
import org.bladerunnerjs.model.exception.name.InvalidRootPackageNameException;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

// TODO: delete this test once we have other spec tests in cutlass-tasks -- this test just proves we can use spec tests outside of brjs-core
public class CreateAppCommandTest extends SpecTest {
	App app;
	App badApp;
	DirNode appJars;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(pluginLocator).hasCommand(new CreateApplicationCommand())
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			badApp = brjs.app("app#$@/");
			appJars = brjs.appJars();
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooFewArguments() throws Exception {
		when(brjs).runCommand("create-app");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'new-app-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception {
		when(brjs).runCommand("create-app", "a", "b", "c");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: c"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAppNameIsNotAValidDirectoryName() throws Exception {
		given(logging).enabled();
		when(brjs).runCommand("create-app", "app#$@/", "appx");
		then(logging).errorMessageReceived(NODE_CREATION_FAILED_LOG_MSG, "App", badApp.dir().getPath())
			.and(exceptions).verifyException(InvalidDirectoryNameException.class, "app#$@/", badApp.dir().getPath())
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAppNamespaceIsNotAValidPackageName() throws Exception {
		when(brjs).runCommand("create-app", "app", "app-x");
		then(exceptions).verifyException(InvalidRootPackageNameException.class, "app-x", app.dir().getPath())
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAppNamespaceIsNotAValidRootPackageName() throws Exception {
		when(brjs).runCommand("create-app", "app", "caplin");
		then(exceptions).verifyException(InvalidRootPackageNameException.class, "caplin", app.dir().getPath())
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAppAlreadyExists() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("create-app", "app", "appx");
		then(exceptions).verifyException(NodeAlreadyExistsException.class, unquoted(app.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void appIsCreatedWhenAllArgumentsAreValid() throws Exception {
		given(appJars).hasBeenCreated()
			.and(logging).enabled();
		when(brjs).runCommand("create-app", "app", "appx");
		then(app).dirExists()
			.and(logging).infoMessageReceived(APP_DEPLOYED_LOG_MSG, app.getName(), app.dir().getPath())
			.and(output).containsLine(APP_CREATED_CONSOLE_MSG, app.getName())
			.and(output).containsLine(APP_DEPLOYED_CONSOLE_MSG, app.getName());
	}
	
	@Test
	public void appCreationConsoleOutputOccursEvenIfAppDeploymentFails() throws Exception {
		when(brjs).runCommand("create-app", "app", "appx");
		then(app).dirExists()
			.and(output).containsLine(APP_CREATED_CONSOLE_MSG, app.getName())
			.and(output).doesNotContain(APP_DEPLOYED_LOG_MSG, app.getName())
			.and(exceptions).verifyException(IllegalStateException.class, appJars.dir().getPath());
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(appJars).hasBeenCreated();
		when(brjs).runCommand("create-app", "app", "appx");
		then(exceptions).verifyNoOutstandingExceptions();
	}
}
