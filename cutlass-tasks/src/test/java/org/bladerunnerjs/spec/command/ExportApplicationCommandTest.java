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
import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.command.export.ExportApplicationCommand;


public class ExportApplicationCommandTest extends SpecTest{
	App app;
	Aspect aspect;
	Bladeset bladeset;
	Blade blade;
	DirNode appJars;
	File sdkDir;
	
	@Before
	public void initTestObjects() throws Exception
	{	
		//TODO::have to create brjs first should remove when moved over to core
		given(brjs).hasBeenCreated();
		
		given(pluginLocator).hasCommand(new ExportApplicationCommand(brjs));
			app = brjs.app("myapp");
			aspect = app.aspect("myaspect");
			bladeset = app.bladeset("mybladeset");
			blade = bladeset.blade("myblade");
			sdkDir = new File(brjs.dir(), "sdk");
	}
	
	@Test
	public void commandThrowsErrorIfAppNameIsNotProvided() throws Exception 
	{
		when(brjs).runCommand("export-app");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'app-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test 
	public void commandThrowsErrorIfUsingAdditionalArguments() throws Exception
	{
		when(brjs).runCommand("export-app", "myapp", "extra", "params");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: params"));
	}
	
	@Test 
	public void commandThrowsErrorIfSpecifiedAppDoesNotExist() throws Exception
	{
		when(brjs).runCommand("export-app", "doesNotExistApp");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted("App 'doesNotExistApp' does not exist"));
	}
	
	@Test
	public void commandAllowsAppToBeExportedWithCorrectContents() throws Exception 
	{
		File sdkDir = new File(brjs.dir(), "sdk");
		
		given(app).hasBeenPopulated();
		when(brjs).runCommand("export-app", "myapp");
		then(sdkDir).containsFile("myapp.zip");
		// TODO - verify zip contents
	}
	
	@Test
	public void defaultDisclaimerIsUsedIfnoDisclaimerIsPassedAsParameter() throws Exception
	{
//		TODO - finish porting over test
	}

}
