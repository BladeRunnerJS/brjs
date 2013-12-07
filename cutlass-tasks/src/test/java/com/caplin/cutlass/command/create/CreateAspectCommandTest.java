package com.caplin.cutlass.command.create;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;

import com.caplin.cutlass.CutlassConfig;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.command.standard.CreateAspectCommand;

import com.caplin.cutlass.util.FileUtility;
import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

public class CreateAspectCommandTest
{
	private static final File testResourcesSdkDir = new File("src/test/resources/CreateBladeCommand");
	
	private File sdkBaseDir;
	private CreateAspectCommand createAspectCommand;
	
	@Before
	public void setup() throws IOException
	{
		File tempDirRoot = FileUtility.createTemporaryDirectory(this.getClass().getSimpleName());
		FileUtils.copyDirectory(testResourcesSdkDir, tempDirRoot);
		tempDirRoot.deleteOnExit();

		sdkBaseDir = new File(tempDirRoot, CutlassConfig.SDK_DIR);
		BRJS brjs = BRJSAccessor.initialize(BRJSTestFactory.createBRJS(sdkBaseDir));
		
		createAspectCommand = new CreateAspectCommand();
		createAspectCommand.setBRJS(brjs);
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfIncorrectNumberArgumentsArePassedIn() throws Exception
	{
		createAspectCommand.doCommand(new String[] { "application" });
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfIncorrectApplicationNameIsPassedIn() throws Exception
	{
		createAspectCommand.doCommand(new String[] { "ghostapp", "mobile" });
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfAspectAlreadyExists() throws Exception
	{
		createAspectCommand.doCommand(new String[] { "fxtrader", "main" });
	}
	
	@Test
	public void commandCopiesOverTemplateAspect() throws Exception
	{
		File newAspectDir = new File(sdkBaseDir.getParentFile(), CutlassConfig.APPLICATIONS_DIR + "/fxtrader/tablet-aspect");
		File aspectSrcFile = new File(newAspectDir, "src/novox/App.js");
		
		assertFalse(newAspectDir.exists());
		assertFalse(aspectSrcFile.exists());
		
		createAspectCommand.doCommand(new String[] { "fxtrader", "tablet" });
		
		assertTrue(newAspectDir.exists());
		assertTrue(aspectSrcFile.exists());
		
		List<String> content = FileUtils.readLines(aspectSrcFile);
		assertEquals(content.get(0),"novox.App = function()");
	}
	
	@Test
	public void commandThrowsErrorIsNotThrownUsingReservedJavaScriptKeyword() throws Exception
	{
		createAspectCommand.doCommand(new String[] { "fxtrader", "var" });
	}
	
	@Test
	public void commandThrowsErrorIsNotThrownIfUsingNumbersInAspectName() throws Exception
	{
		createAspectCommand.doCommand(new String[] { "fxtrader", "common1-aspect" });
	}
}
