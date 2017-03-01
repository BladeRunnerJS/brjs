package org.bladerunnerjs.spec.command;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.api.spec.exception.BrowserStartupException;
import org.bladerunnerjs.legacy.command.test.TestCommand;
import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class TestCommandTest extends SpecTest
{
	private App app;
	private Bladeset bladeset;
	private File sdkDir;
	private File secondaryTempFolder = null;
	private String testRunnerConfContents;
	
	@Before
	public void initTestObjects() throws Exception
	{	
		secondaryTempFolder = org.bladerunnerjs.utility.FileUtils.createTemporaryDirectory(TestCommandTest.class);
		//TODO::have to create brjs first should remove when moved over to core
		given(brjs).hasCommandPlugins(new TestCommand());
		given(brjs).hasBeenCreatedWithWorkingDir(secondaryTempFolder);
		ThreadSafeStaticBRJSAccessor.initializeModel(brjs);
		
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
	
	@After
	public void tearDown() {
		FileUtils.deleteQuietly(secondaryTempFolder);
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
		then(exceptions).verifyException(BrowserStartupException.class, 
				unquoted("Could not find the browser on disk. Please check your test config inside\n '" + sdkDir.getParentFile().getPath() + "/conf'"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfAppIsNotInTheKnownAppsLocation() throws Exception {
		given(secondaryTempFolder).containsFiles("myapp/index.html", "myapp/app.conf")
			.and(brjs).containsFile("conf/test-runner.conf")
			.and(brjs).hasBeenCreatedWithWorkingDir(secondaryTempFolder);
		when(brjs).runCommand("test", "myapp");
		then(exceptions).verifyException(CommandArgumentsException.class, brjs.appsFolder().getAbsolutePath());
	}
	
}
