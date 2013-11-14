package org.bladerunnerjs.spec.brjs;

import org.bladerunnerjs.core.plugin.command.standard.CreateBladeCommand;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.bladerunnerjs.testing.utility.ExplodingCommand;
import org.junit.Before;
import org.junit.Test;

public class UserCommandTest extends SpecTest {
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).hasBeenCreated()
			.and(brjs).containsFileWithContents("sdk/version.txt", "{'Version': 'the-version', 'BuildDate': 'the-build-date'}")
			.and(brjs).hasCommands(new CreateBladeCommand(), new ExplodingCommand());
	}
	
	@Test
	public void messageIsDisplayedIfRunUserCommandIsInvokedWithANonExistentCommandName() {
		when(brjs).runUserCommand("no-such-command");
		then(output).containsText(
			"BladeRunner version: the-version, built: the-build-date",
			"",
			"No such command 'no-such-command'");
	}
	
	@Test
	public void usageIsDisplayedIfIncorrectArgumentsAreProvided() {
		when(brjs).runUserCommand("create-blade");
		then(output).containsText(
			"BladeRunner version: the-version, built: the-build-date",
			"",
			"Problem:",
			"  Parameter 'new-blade-name' is required.",
			"  Parameter 'target-app-name' is required.",
			"  Parameter 'target-bladeset-name' is required.",
			"",
			"Usage:",
			"  brjs create-blade <target-app-name> <target-bladeset-name> <new-blade-name>");
	}
	
	@Test
	public void stackTraceIsDisplayedIfArgumentsAreInvalid() {
		when(brjs).runUserCommand("explode");
		then(output).containsText(
			"BladeRunner version: the-version, built: the-build-date",
			"",
			"Error:",
			"  Bang!",
			"",
			"Stack Trace:",
			"org.bladerunnerjs.model.exception.command.CommandOperationException: Bang!")
			.and(exceptions).verifyException(CommandOperationException.class);
	}
	
	// TODO: add some tests around the --verbose and --debug flags
}
