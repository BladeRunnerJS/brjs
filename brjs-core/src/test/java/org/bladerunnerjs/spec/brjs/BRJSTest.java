package org.bladerunnerjs.spec.brjs;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.exception.command.NoSuchCommandException;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.bladerunnerjs.testing.utility.MockCommand;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class BRJSTest extends SpecTest {
	private NamedDirNode brjsTemplate;
	private App app1;
	private App app2;
	
	MockCommand command1;
	MockCommand command2;
	
	@Before
	public void initTestObjects() throws Exception
	{
		command1 = new MockCommand("command1", "Command #1 description.", "command-usage", "Command #1 help.");
		command2 = new MockCommand("command2", "Command #2 description.", "command-usage", "Command #2 help.");
		
		given(brjs).hasCommand(command1)
			.and(brjs).hasBeenCreated()
			.and(brjs).containsFileWithContents("sdk/version.txt", "{'Version': 'the-version', 'BuildDate': 'the-build-date'}");
        	brjsTemplate = brjs.template("brjs");
        	app1 = brjs.app("app1");
        	app2 = brjs.app("app2");
	}
	
	@Test
	public void theAppConfIsWrittenOnPopulate() throws Exception {
		given(brjsTemplate).hasBeenCreated();
		when(brjs).populate();
		then(brjs).fileHasContents("conf/bladerunner.conf", "defaultInputEncoding: UTF-8\ndefaultOutputEncoding: UTF-8\njettyPort: 7070\nloginRealm: BladeRunnerLoginRealm");
	}
	
	@Test
	public void observersArentNotifiedOfEventsThatArentBeneathThem() throws Exception {
		given(observer).observing(app2);
		when(app1).populate();
		then(observer).noNotifications();
	}
	
	@Test
	public void exceptionIsThrownIfRunCommandIsInvokedWithoutACommandName() throws Exception {
		when(brjs).runCommand("");
		then(exceptions).verifyException(NoSuchCommandException.class);
	}
	
	@Test
	public void helpMenuIsShownIfRunUserCommandIsInvokedWithoutACommandName() throws Exception {
		when(brjs).runUserCommand("");
    	then(output).containsText(
			"BladeRunnerJS version: the-version, built: the-build-date",
			"",
			"Possible commands:",
			"  command1     :Command #1 description.                ",
			"  -----",
			"  help         :Prints this list of commands           ",
			"  version      :Displays the BladeRunnerJS version     ",
			"",
			"Supported flags:",
			"  --quiet",
			"  --verbose",
			"  --debug");
	}
	
	@Test
	public void exceptionIsThrownIfRunCommandIsInvokedWithANonExistentCommandName() throws Exception {
		when(brjs).runCommand("no-such-command");
		then(exceptions).verifyException(NoSuchCommandException.class, "no-such-command");
	}
	
}
