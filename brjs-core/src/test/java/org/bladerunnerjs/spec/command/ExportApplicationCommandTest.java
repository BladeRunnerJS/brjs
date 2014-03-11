package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.plugin.plugins.commands.standard.ExportApplicationCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class ExportApplicationCommandTest extends SpecTest {
	private App app;
	private Aspect aspect;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommands(new ExportApplicationCommand())
			.and(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
	}
	
	@Test
	public void exceptionIsThrownIfAppDoesNotExist() throws Exception {
		when(brjs).runCommand("export-app", "doesNotExist");
		then(exceptions).verifyException(CommandArgumentsException.class, unquoted("Could not find application 'doesNotExist'"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTooManyParametersAreProvidedd() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("export-app", "app1", "bla", "blabla");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: blabla"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void appCanBeExported() throws Exception {
		given(app).hasBeenCreated()
			.and(aspect).classFileHasContent("appns.Class1", "default aspect src");
		when(brjs).runCommand("export-app", "app1");
		then(brjs).hasFile("generated/exported-app/app1.zip");
	}
	
	// TODO port over other tests

}
