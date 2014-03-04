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
import com.caplin.cutlass.util.FileUtility;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.plugins.commands.standard.CreateBladesetCommand;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

public class CreateBladesetCommandTest
{
	private static final File testResourcesSdkDir = new File("src/test/resources/CreateBladeCommand");
	
	private File sdkBaseDir;
	private CreateBladesetCommand createBladesetCommand;
	private File newBladesetToBeCreatedDirectory;
	
	@Before
	public void setup() throws IOException
	{
		File tempDirRoot = FileUtility.createTemporaryDirectory(this.getClass().getSimpleName());

		FileUtils.copyDirectory(testResourcesSdkDir, tempDirRoot);
		sdkBaseDir = new File(tempDirRoot, CutlassConfig.SDK_DIR);
		BRJS brjs = BRJSAccessor.initialize(BRJSTestFactory.createBRJS(sdkBaseDir));
		createBladesetCommand = new CreateBladesetCommand();
		createBladesetCommand.setBRJS(brjs);
		
		newBladesetToBeCreatedDirectory = new File(sdkBaseDir.getParentFile(), CutlassConfig.APPLICATIONS_DIR + "/fxtrader/basic-bladeset");
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfIncorrectNumberArgumentsArePassedIn() throws Exception
	{
		createBladesetCommand.doCommand(new String[] { "application" });
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfIncorrectApplicationNameIsPassedIn() throws Exception
	{
		createBladesetCommand.doCommand(new String[] { "fitrader", "fx" });
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfBladesetAlreadyExists() throws Exception
	{
		createBladesetCommand.doCommand(new String[] { "fxtrader", "existing" });
	}
	
	@Test
	public void commandCopiesOverTemplateBladeset() throws Exception
	{
		File bladeSourceFile = new File(newBladesetToBeCreatedDirectory, "src/novox/basic/ExampleClass.js");
		assertFalse(newBladesetToBeCreatedDirectory.exists());
		assertFalse(bladeSourceFile.exists());
		
		createBladesetCommand.doCommand(new String[] { "fxtrader", "basic" });
		
		assertTrue(bladeSourceFile.exists());
		assertTrue(newBladesetToBeCreatedDirectory.exists());
		
		List<String> content = FileUtils.readLines(bladeSourceFile);
		assertEquals(content.get(2),"novox.basic.ExampleClass = function()");
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfUsingUpperCaseBladesetName() throws Exception
	{
		createBladesetCommand.doCommand(new String[] { "fxtrader", "BASIC" });
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfUsingReservedJavaScriptKeyword() throws Exception
	{
		createBladesetCommand.doCommand(new String[] { "fxtrader", "default" });
	}
	
	@Test
	public void commandAllowsNumbersInBladesetName() throws Exception
	{
		createBladesetCommand.doCommand(new String[] { "fxtrader", "common1" });
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfUsingHyphenInBladesetName() throws Exception
	{
		createBladesetCommand.doCommand(new String[] { "fxtrader", "cool-blue" });
	}
}
