package com.caplin.cutlass.command.copy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.bladerunnerjs.model.TestModelAccessor;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;

import com.caplin.cutlass.util.FileUtility;

import org.bladerunnerjs.model.StaticModelAccessor;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;
import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

public class CopyBladesetCommandTest extends TestModelAccessor
{

	private static final File testDirectory = new File("src/test/resources/CopyBladesetCommandTest");
	private CopyBladesetCommand copyBladesetCommand;
	private File tempSdkBaseDir, tempDir, targetBladesetDirectory, targetBladeJsFile, targetFxBladesetDirectory, targetFxBladeJsFile;
	
	@Before
	public void setUp() throws Exception 
	{		
		tempDir = FileUtility.createTemporaryDirectory("CopyBladesetCommandTest");
		tempDir.deleteOnExit();
		FileUtils.copyDirectory(testDirectory.getAbsoluteFile(), tempDir);
		
		tempSdkBaseDir = new File(tempDir, SDK_DIR);
		StaticModelAccessor.destroy();
		StaticModelAccessor.initializeModel(createModel(tempSdkBaseDir));
		
		copyBladesetCommand = new CopyBladesetCommand(tempSdkBaseDir);
		
		targetBladesetDirectory = new File(tempDir, APPLICATIONS_DIR + "/targetApplication/a-bladeset");
		targetBladeJsFile = new File(targetBladesetDirectory, "blades/a-blade/src/novox/a/a-blade/ABladeClass.js");
		
		targetFxBladesetDirectory = new File(tempDir, APPLICATIONS_DIR + "/targetApplication/fx-bladeset");
		targetFxBladeJsFile = new File(targetFxBladesetDirectory, "blades/a-blade/src/novox/fx/a-blade/ABladeClass.js");
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfIncorrectNumberArgumentsArePassedIn() throws Exception
	{
		copyBladesetCommand.doCommand(new String[] { "sourceApplication", "targetApplication" });
	}
	
	@Test (expected=CommandOperationException.class)
	public void commandThrowsErrorIfTargetBladesetAlreadyExists() throws Exception
	{
		copyBladesetCommand.doCommand(new String[] { "sourceApplication", "b", "targetApplication" });
	}
	
	@Test
	public void commandMovesOverBladeset() throws Exception
	{
		assertFalse(targetBladesetDirectory.exists());
		assertFalse(targetBladeJsFile.exists());
		
		copyBladesetCommand.doCommand(new String[] { "sourceApplication", "a", "targetApplication" });
		
		assertTrue(targetBladesetDirectory.exists());
		assertTrue(targetBladeJsFile.exists());
	}
	
	@Test
	public void commandMovesOverBladesetWhenSpecifyingOptionalTargetBladesetName() throws Exception
	{
		assertFalse(targetFxBladesetDirectory.exists());
		assertFalse(targetFxBladeJsFile.exists());
		
		copyBladesetCommand.doCommand(new String[] { "sourceApplication", "a", "targetApplication", "fx-bladeset" });
		
		assertTrue(targetFxBladesetDirectory.getAbsolutePath() + " does not exist", targetFxBladesetDirectory.exists());
		assertTrue(targetFxBladeJsFile.getAbsolutePath() + " does not exist", targetFxBladeJsFile.exists());
	}
	
	@Test
	public void commandMovesOverBladesetWhenSpecifyingSourceBladesetNameWithHyphenBladesetSuffix() throws Exception
	{
		assertFalse(targetFxBladesetDirectory.exists());
		assertFalse(targetFxBladeJsFile.exists());
		
		copyBladesetCommand.doCommand(new String[] { "sourceApplication", "a-bladeset", "targetApplication", "fx" });
		
		assertTrue(targetFxBladesetDirectory.getAbsolutePath() + " does not exist", targetFxBladesetDirectory.exists());
		assertTrue(targetFxBladeJsFile.getAbsolutePath() + " does not exist", targetFxBladeJsFile.exists());
	}
	
	@Test
	public void commandMovesOverBladesetWhenSpecifyingOptionalTargetBladesetNameWithHyphenBladesetSuffix() throws Exception
	{
		assertFalse(targetFxBladesetDirectory.exists());
		assertFalse(targetFxBladeJsFile.exists());
		
		copyBladesetCommand.doCommand(new String[] { "sourceApplication", "a", "targetApplication", "fx-bladeset" });
		
		assertTrue(targetFxBladesetDirectory.exists());
		assertTrue(targetFxBladeJsFile.exists());
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandMovesOverBladesetWhenSpecifyingBladesetNameWithMultipleHyphens() throws Exception
	{
		copyBladesetCommand.doCommand(new String[] { "sourceApplication", "a", "targetApplication", "my-hyphen-bladeset" });
	}
	
	@Test
	public void commandAllowsBladesetThatContainsANumber() throws Exception
	{
		copyBladesetCommand.doCommand(new String[] { "sourceApplication", "a", "targetApplication", "fx2" });
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsExceptionWhenBladesetContainsAnUnderscore() throws Exception
	{
		copyBladesetCommand.doCommand(new String[] { "sourceApplication", "a", "targetApplication", "new_fx" });
	}
	
	@Test (expected=CommandOperationException.class)
	public void commandMovesOverBladesetWhenSpecifyingBladesetNameWhichAlreadyExists() throws Exception
	{
		assertFalse(targetBladesetDirectory.exists());
		assertFalse(targetBladeJsFile.exists());
		
		copyBladesetCommand.doCommand(new String[] { "sourceApplication", "a", "targetApplication", "b" });
	}
	
	@Test (expected=CommandOperationException.class)
	public void commandThrowsErrorIfSourceBladesetDoesntExist() throws Exception
	{
		copyBladesetCommand.doCommand(new String[] { "sourceApplication", "b", "targetApplication" });
	}
	
	
	
}
