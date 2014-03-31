package org.bladerunnerjs.spec.command;

import java.io.File;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import com.caplin.cutlass.command.test.testrunner.BrowserIOException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.caplin.cutlass.command.test.TestCommand;


public class TestCommandTest extends SpecTest
{
	private App app;
	private Bladeset bladeset;
	private File sdkDir;
	
	private String testRunnerConfContents;
	
	@Before
	public void initTestObjects() throws Exception
	{	
		//TODO::have to create brjs first should remove when moved over to core
		given(brjs).hasBeenCreated();
		
		given(brjs).hasCommands(new TestCommand());
			app = brjs.app("myapp");
			app.aspect("myaspect");
			bladeset = app.bladeset("mybladeset");
			bladeset.blade("myblade");
			sdkDir = new File(brjs.dir(), "sdk");
			
			testRunnerConfContents  = 
				"jsTestDriverJar: JsTestDriver.jar\n" +
				"portNumber: 4224\n" +
				"defaultBrowser: badBrowser\n" +
				"browserPaths:\n" +
				"   windows:\n" +
				"    ff: badPath.exe";
	}
	
	// TODO Remove the @Ignore once we are able to allow jsTestDriver on the path for the specTests
	@Ignore
	@Test
	public void commandThrowsErrorIfInvalidBrowserDefined() throws Exception 
	{
		given(brjs).containsFile("sdk/templates/brjs-template/conf/test-runner.conf")
			.and(brjs).containsFileWithContents("conf/test-runner.conf", testRunnerConfContents)
			.and(app).hasBeenCreated();

		when(brjs).runCommand("test", "../apps");
		then(exceptions).verifyException(BrowserIOException.class, 
				unquoted("Could not find the browser on disk. Please check your test config inside\n '" + sdkDir.getParentFile().getPath() + "/conf'"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
}
