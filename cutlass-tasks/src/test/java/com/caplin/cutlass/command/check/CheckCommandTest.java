package com.caplin.cutlass.command.check;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.util.FileUtility;
import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;
import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

public class CheckCommandTest
{
	private final File TWO_APPS_WITH_DIFF_JARS_AND_PATCHES = new File("src/test/resources/CheckCommandTest/two-apps-with-diff-jars-and-patches");
	private final File SINGLE_CLEAN_APP = new File("src/test/resources/CheckCommandTest/single-clean-app");
	private final File TWO_APPS_WITH_SDK_THIRDPARTY_LIB_OVERRIDE = new File("src/test/resources/CheckCommandTest/two-apps-with-sdk-thirdparty-lib-override");
	private final File SINGLE_APP_WITH_NO_SDK_THIRDPARTY = new File("src/test/resources/CheckCommandTest/single-app-with-no-sdk-thirdparty");
	private File tempDir;
	
		
	private ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
	
	@Before
	public void setUp() throws IOException 
	{		
		tempDir = FileUtility.createTemporaryDirectory("CheckCommandTest");
		tempDir.deleteOnExit();
	}
	
	@Test
	public void testCheckCommandWithTwoAppsAndPatchesFile() throws Exception
	{
		FileUtils.copyDirectory(TWO_APPS_WITH_DIFF_JARS_AND_PATCHES, tempDir);
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(tempDir, new PrintStream(byteStream)));
		CheckCommand checkCommand = new CheckCommand();
		
		checkCommand.doCommand( new String[] {""} );
		
		String messageString = byteStream.toString();
		assertEquals(3, messageString.split("The following jars should be added to this apps' \"WEB-INF/lib\" from the SDK:").length);
		assertEquals(3, messageString.split("The following jars should be deleted from this apps' \"WEB-INF/lib\":").length);
		assertEquals(2, messageString.split("The following jars have been modified inside this apps' \"WEB-INF/lib\":").length);
	}
	
	@Test
	public void testCheckThatWeFindJsPatchFilesInRootDirAndAllSubDirectories() throws Exception
	{
		FileUtils.copyDirectory(TWO_APPS_WITH_DIFF_JARS_AND_PATCHES, tempDir);
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(tempDir, new PrintStream(byteStream)));
		CheckCommand checkCommand = new CheckCommand();
		
		checkCommand.doCommand( new String[] {""} );
		
		String messageString = byteStream.toString();
		
		String patchesMessage = messageString.split("Patch files were found inside the 'js-patches' directory, please check to see if they are still necessary:")[1];
		
		assertTrue(patchesMessage.contains("patch.js"));
		assertTrue(patchesMessage.contains("hack.js"));
		assertTrue(patchesMessage.contains("not-picked-up.js"));	
	}

	@Test
	public void testCheckCommandWithSingleCleanApp() throws Exception
	{
		FileUtils.copyDirectory(SINGLE_CLEAN_APP, tempDir);
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(tempDir, new PrintStream(byteStream)));
		BRJSAccessor.root.jsPatches().create();
		CheckCommand checkCommand = new CheckCommand();
		
		checkCommand.doCommand( new String[] {""} );
		
		String messageString = byteStream.toString();

		assertTrue(messageString.contains("Jar consistency check - OK"));
		assertFalse(messageString.contains("js-patches"));
	}
	
	@Test
	public void testCheckCommandWhenOneAppOverridesThirdParty() throws Exception
	{
		FileUtils.copyDirectory(TWO_APPS_WITH_SDK_THIRDPARTY_LIB_OVERRIDE, tempDir);
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(tempDir, new PrintStream(byteStream)));
		CheckCommand checkCommand = new CheckCommand();
		
		assertTrue(new File(tempDir, APPLICATIONS_DIR + "/firstapp/libs/jQuery").exists());
		assertTrue(new File(tempDir, APPLICATIONS_DIR + "/firstapp/libs/Sencha").exists());
		assertTrue(new File(tempDir, SDK_DIR + "/libs/javascript/jQuery").exists());
		assertFalse(new File(tempDir, SDK_DIR + "/libs/javascript/Sencha").exists());
		
		checkCommand.doCommand( new String[] {""} );
		
		String messageString = byteStream.toString();
		
		assertTrue(messageString.contains("The following libs also exist inside the SDK:"));
		assertTrue(messageString.contains("jQuery"));
		assertFalse(messageString.contains("Sencha"));

	}
	
	@Test
	public void testCheckCommandWithNoSdkThirdparty() throws Exception
	{
		FileUtils.copyDirectory(SINGLE_APP_WITH_NO_SDK_THIRDPARTY, tempDir);
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(tempDir, new PrintStream(byteStream)));
		CheckCommand checkCommand = new CheckCommand();
		
		assertTrue(new File(tempDir, APPLICATIONS_DIR + "/firstapp/thirdparty-libraries/jQuery").exists());
		assertTrue(new File(tempDir, APPLICATIONS_DIR + "/firstapp/thirdparty-libraries/Sencha").exists());
		
		checkCommand.doCommand( new String[] {""} );
		
		String messageString = byteStream.toString();
		
		assertTrue(messageString.contains("There are no thirdparty libraries present in the SDK."));		
	}
	
	
}
