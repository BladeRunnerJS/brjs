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

import org.bladerunnerjs.core.plugin.command.standard.CreateBladeCommand;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.util.FileUtility;
import org.bladerunnerjs.model.BRJS;
import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

public class CreateBladeCommandTest
{
	private static final File testResourcesSdkDir = new File("src/test/resources/CreateBladeCommand");
	
	private File sdkBaseDir;
	
	private CreateBladeCommand createBladeCommand;
	private File fxBladesetBladesDirectory;
	
	@Before
	public void setup() throws IOException
	{
		File tempDirRoot = FileUtility.createTemporaryDirectory(this.getClass().getSimpleName());

		FileUtils.copyDirectory(testResourcesSdkDir, tempDirRoot);
		sdkBaseDir = new File(tempDirRoot, CutlassConfig.SDK_DIR);
		BRJS brjs = BRJSAccessor.initialize(BRJSTestFactory.createBRJS(sdkBaseDir));
		
		createBladeCommand = new CreateBladeCommand();
		createBladeCommand.setBRJS(brjs);
		
		fxBladesetBladesDirectory = new File(sdkBaseDir.getParentFile(), CutlassConfig.APPLICATIONS_DIR +"/fxtrader/fx-bladeset/blades");
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfIncorrectNumberArgumentsArePassedIn() throws Exception
	{
		createBladeCommand.doCommand(new String[] { "application" });
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfIncorrectApplicationNameIsPassedIn() throws Exception
	{
		createBladeCommand.doCommand(new String[] { "fitrader", "fx", "grid" });
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfIncorrectBladesetNameIsPassedIn() throws Exception
	{
		createBladeCommand.doCommand(new String[] { "fxtrader", "fi", "grid" });
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfBladeAlreadyExists() throws Exception
	{
		createBladeCommand.doCommand(new String[] { "fxtrader", "existing", "existing" });
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfTheBladeNameIsReservedJavaScriptKeyword() throws Exception
	{
		createBladeCommand.doCommand(new String[] { "fxtrader", "fx", "default" });
	}
	
	@Test
	public void commandCopiesOverTemplateBlade() throws Exception
	{
		File bladeSourceFile = new File(fxBladesetBladesDirectory, "grid/src/novox/fx/grid/ExampleClass.js");
		assertFalse(fxBladesetBladesDirectory.exists());
		assertFalse(bladeSourceFile.exists());
		
		createBladeCommand.doCommand(new String[] { "fxtrader", "fx", "grid" });
		
		assertTrue(bladeSourceFile.exists());
		assertTrue(fxBladesetBladesDirectory.exists());
		
		List<String> content = FileUtils.readLines(bladeSourceFile);
		assertEquals(content.get(2),"novox.fx.grid.ExampleClass = function()");
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfUsingUpperCaseBladeName() throws Exception
	{
		createBladeCommand.doCommand(new String[] { "fxtrader", "fx", "GRID" });
	}
	
	@Test
	public void commandAllowsNumbersInBladeName() throws Exception
	{
		createBladeCommand.doCommand(new String[] { "fxtrader", "fx", "grid1" });
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfUsingHyphenInBladeName() throws Exception
	{
		createBladeCommand.doCommand(new String[] { "fxtrader", "fx", "my-grid" });
	}
}
