package org.bladerunnerjs.spec.command;

import java.io.File;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.model.exception.test.BrowserNotFoundException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.caplin.cutlass.command.export.ExportApplicationCommand;
import com.caplin.cutlass.command.test.TestCommand;
import com.caplin.cutlass.command.test.TestServerCommand;


public class TestServerCommandTest extends SpecTest
{
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private DirNode appJars;
	private File sdkDir;
	
	private String testRunnerConfContents;
	
	@Before
	public void initTestObjects() throws Exception
	{	
		//TODO::have to create brjs first should remove when moved over to core
		given(brjs).hasBeenCreated();
		
		given(pluginLocator).hasCommand(new TestServerCommand());
			app = brjs.app("myapp");
			aspect = app.aspect("myaspect");
			bladeset = app.bladeset("mybladeset");
			blade = bladeset.blade("myblade");
			sdkDir = new File(brjs.dir(), "sdk");
	}
	
	// #183 - TODO Remove the @Ignore once we are able to allow jsTestDriver on the path for the specTests
	@Ignore
	@Test
	public void canLaunchTestServerWithoutBrowsersConfiguredUsingTheNoBrowserFlag() throws Exception 
	{
		given(brjs).containsFile("sdk/templates/brjs-template/conf/test-runner.conf")
			.and(brjs).containsFileWithContents("conf/test-runner.conf", "bad test-runner.conf contents");
		when(brjs).runCommand("test-server", "--no-browser");
		then(exceptions).verifyNoOutstandingExceptions();
	}
}
