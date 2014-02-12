package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.plugin.plugins.commands.standard.WarCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class WARCommandTest extends SpecTest {
	App app;
	Aspect aspect;
	DirNode appJars;
	
	@Before
	public void initTestObjects() throws Exception
	{	
		//TODO::have to create brjs first should remove when moved over to core
		given(brjs).hasBeenCreated();
		
		given(brjs).hasCommands(new WarCommand())
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			aspect = app.aspect("myaspect");
	}
	
	@Ignore
	@Test
	public void exceptionIsThrownIfThereAreTooFewArguments() throws Exception {
		when(brjs).runCommand("war");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'new-aspect-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}

}
