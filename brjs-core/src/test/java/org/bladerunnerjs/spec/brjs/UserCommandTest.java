package org.bladerunnerjs.spec.brjs;

import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.plugin.plugins.commands.standard.CreateBladeCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.utility.ExplodingCommand;
import org.junit.Before;
import org.junit.Test;

public class UserCommandTest extends SpecTest {
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).hasCommandPlugins(new CreateBladeCommand(), new ExplodingCommand())
			.and(brjs).hasBeenCreated()
			.and(brjs).containsFileWithContents("sdk/version.txt", "{'Version': 'the-version', 'BuildDate': 'the-build-date'}");
	}
	
	@Test
	public void messageIsDisplayedIfRunUserCommandIsInvokedWithANonExistentCommandName() {
		when(brjs).runUserCommand("no-such-command");
		then(logging).containsConsoleText(
			"No such command 'no-such-command'",
			"--------",
			"");
	}
	
	@Test
	public void usageIsDisplayedIfIncorrectArgumentsAreProvided() {
		when(brjs).runUserCommand("create-blade");
		then(logging).containsConsoleText(
			"Problem:",
			"  Parameter 'target-app-name' is required.",
			"  Parameter 'new-blade-name' is required.",
			"",
			"Usage:",
			"  brjs create-blade <target-app-name> <new-blade-name> [(-s|--bladeset) <bladeset>]");
	}
	
	@Test
	public void stackTraceIsDisplayedIfArgumentsAreInvalid() {
		when(brjs).runUserCommand("explode");
		then(logging).containsConsoleText(
			"Error:",
			"  Bang!",
			"",
			"Stack Trace:",
			"org.bladerunnerjs.model.exception.command.CommandOperationException: Bang!")
			.and(exceptions).verifyException(CommandOperationException.class);
	}
	
	// TODO: add some tests around the --info and --debug flags
}
