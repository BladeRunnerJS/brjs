package org.bladerunnerjs.spec.command;

import static org.bladerunnerjs.plugin.plugins.commands.standard.BuildAppCommand.Messages.*;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.DirectoryDoesNotExistException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.plugin.plugins.commands.standard.BuildAppCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class BuildAppCommandTest extends SpecTest {
	App app;
	Bladeset bladeset;
	Blade blade;
	Blade badBlade;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommands(new BuildAppCommand())
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			bladeset = app.bladeset("bladeset");
			blade = bladeset.blade("blade");
			badBlade = bladeset.blade("!$%$^");
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooFewArguments() throws Exception {
		when(brjs).runCommand("build-app");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'app-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAppDoesntExist() throws Exception {
		when(brjs).runCommand("build-app", "app");
		then(exceptions).verifyException(NodeDoesNotExistException.class, "app", unquoted(app.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfAnInvalidTargetDirIsProvided() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("build-app", "app", "target");
		then(exceptions).verifyException(DirectoryDoesNotExistException.class, "target")
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void appIsExportedToSdkDirByDefault() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("build-app", "app");
		then(brjs).hasDir("sdk/app")
			.and(output).containsLine(APP_BUILT_CONSOLE_MSG, "app", brjs.file("sdk/app").getCanonicalPath());
	}
	
	@Test
	public void appCanBeExportedToASpecifiedDirectory() throws Exception {
		given(app).hasBeenCreated()
			.and(brjs).hasDir("sdk/target");
		when(brjs).runCommand("build-app", "app", "target");
		then(brjs).hasDir("sdk/target/app")
			.and(output).containsLine(APP_BUILT_CONSOLE_MSG, "app", brjs.file("sdk/target/app").getCanonicalPath());
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(app).hasBeenCreated();
		when(brjs).runCommand("build-app", "app");
		then(exceptions).verifyNoOutstandingExceptions();
	}
}
