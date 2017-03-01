package org.bladerunnerjs.spec.command;

import java.io.File;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.api.spec.exception.NoBrowsersDefinedException;
import org.bladerunnerjs.legacy.command.test.TestServerCommand;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class TestServerCommandTest extends SpecTest
{
	
	private String testRunnerConfWithoutBrowsersDefined;
	
	@Before
	public void initTestObjects() throws Exception
	{	
		//TODO: have to create brjs first should remove when moved over to core
		given(brjs).hasBeenCreated();
		
		given(brjs).hasCommandPlugins(new TestServerCommand());
			App app = brjs.app("myapp");
			app.aspect("myaspect");
			Bladeset bladeset = app.bladeset("mybladeset");
			bladeset.blade("myblade");
			new File(brjs.dir(), "sdk");
			
		testRunnerConfWithoutBrowsersDefined = 
				"jsTestDriverJar: pathToJsTestDriver.jar\n" +
				"portNumber: 4224\n" +
				"defaultBrowser: chrome\n" +
				"\n" +
				"browserPaths:\n" + 
				"  windows:\n" +
				"  mac:" +
				"  linux:";
	}
	
	// #183 - TODO: Remove the @Ignore once we are able to allow jsTestDriver on the path for the specTests
	@Ignore
	@Test
	public void canLaunchTestServerWithoutBrowsersConfiguredUsingTheNoBrowserFlag() throws Exception 
	{
		given(brjs).containsFileWithContents("sdk/templates/brjs-template/conf/test-runner.conf", testRunnerConfWithoutBrowsersDefined)
			.and(brjs).containsFileWithContents("conf/test-runner.conf", testRunnerConfWithoutBrowsersDefined);
		when(brjs).runCommand("test-server", "--no-browser");
		then(exceptions).verifyNoOutstandingExceptions();
//			.and(processes).testServerProcessesStarted(); //TODO: add the ability to test whether a process (or mock process) has been started by the test runner
	}
	
	@Ignore
	@Test
	public void throwsNoBrowsersDefinedExceptionWhenNoBrowsersAreDefined() throws Exception
	{
		given(brjs).containsFileWithContents("sdk/templates/brjs-template/conf/test-runner.conf", testRunnerConfWithoutBrowsersDefined)
			.and(brjs).containsFileWithContents("conf/test-runner.conf", testRunnerConfWithoutBrowsersDefined);
		when(brjs).runCommand("test-server");
		then(exceptions).verifyException(NoBrowsersDefinedException.class).
			whereTopLevelExceptionIs(CommandOperationException.class);
//			.and(processes).testServerProcessesNotStarted(); //TODO: add the ability to test whether a process (or mock process) is not running
	}
}
