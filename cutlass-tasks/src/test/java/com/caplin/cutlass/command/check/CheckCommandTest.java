package com.caplin.cutlass.command.check;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.TestModelAccessor;
import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.util.FileUtility;

import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.testing.utility.LogMessageStore;
import org.bladerunnerjs.testing.utility.TestLoggerFactory;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;
import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

public class CheckCommandTest extends TestModelAccessor
{
	private final File TWO_APPS_WITH_DIFF_JARS_AND_PATCHES = new File("src/test/resources/CheckCommandTest/two-apps-with-diff-jars-and-patches");
	private final File SINGLE_CLEAN_APP = new File("src/test/resources/CheckCommandTest/single-clean-app");
	private final File TWO_APPS_WITH_SDK_THIRDPARTY_LIB_OVERRIDE = new File("src/test/resources/CheckCommandTest/two-apps-with-sdk-thirdparty-lib-override");
	private final File SINGLE_APP_WITH_NO_SDK_THIRDPARTY = new File("src/test/resources/CheckCommandTest/single-app-with-no-sdk-thirdparty");
	private File tempDir;
	
	private LogMessageStore logStore;
	
	@Before
	public void setUp() throws IOException 
	{	
		logStore = new LogMessageStore(true);
		tempDir = FileUtility.createTemporaryDirectory("CheckCommandTest");
		tempDir.deleteOnExit();
	}
	
	@Test
	public void testCheckCommandWithTwoAppsAndPatchesFile() throws Exception
	{
		FileUtils.copyDirectory(TWO_APPS_WITH_DIFF_JARS_AND_PATCHES, tempDir);
		initModel(tempDir);
		CheckCommand checkCommand = new CheckCommand();
		
		checkCommand.doCommand( new String[] {""} );
		
		logStore.verifyConsoleLogMessages("The following jars have been modified inside this apps' \"WEB-INF/lib\":");
		logStore.verifyConsoleLogMessages("The following jars should be added to this apps' \"WEB-INF/lib\" from the SDK:");
	}
	
	@Test
	public void testCheckThatWeFindJsPatchFilesInRootDirAndAllSubDirectories() throws Exception
	{
		FileUtils.copyDirectory(TWO_APPS_WITH_DIFF_JARS_AND_PATCHES, tempDir);
		initModel(tempDir);
		CheckCommand checkCommand = new CheckCommand();
		
		checkCommand.doCommand( new String[] {""} );
		
		logStore.verifyConsoleLogMessages("patch.js");
		logStore.verifyConsoleLogMessages("hack.js");
		logStore.verifyConsoleLogMessages("not-picked-up.js");	
	}

	@Test
	public void testCheckCommandWithSingleCleanApp() throws Exception
	{
		FileUtils.copyDirectory(SINGLE_CLEAN_APP, tempDir);
		initModel(tempDir);
		ThreadSafeStaticBRJSAccessor.root.jsPatches().create();
		CheckCommand checkCommand = new CheckCommand();
		
		checkCommand.doCommand( new String[] {""} );

		logStore.verifyConsoleLogMessages("Jar consistency check - OK");
		logStore.verifyConsoleLogDoesNotContain("js-patches");
	}
	
	@Test
	public void testCheckCommandWhenOneAppOverridesThirdParty() throws Exception
	{
		FileUtils.copyDirectory(TWO_APPS_WITH_SDK_THIRDPARTY_LIB_OVERRIDE, tempDir);
		initModel(tempDir);
		CheckCommand checkCommand = new CheckCommand();
		
		assertTrue(new File(tempDir, APPLICATIONS_DIR + "/firstapp/libs/jQuery").exists());
		assertTrue(new File(tempDir, APPLICATIONS_DIR + "/firstapp/libs/Sencha").exists());
		assertTrue(new File(tempDir, SDK_DIR + "/libs/javascript/jQuery").exists());
		assertFalse(new File(tempDir, SDK_DIR + "/libs/javascript/Sencha").exists());
		
		checkCommand.doCommand( new String[] {""} );
		
		logStore.verifyConsoleLogMessages("The following libs also exist inside the SDK:");
		logStore.verifyConsoleLogMessages("jQuery");
		logStore.verifyConsoleLogDoesNotContain("Sencha");

	}
	
	@Test
	public void testCheckCommandWithNoSdkThirdparty() throws Exception
	{
		FileUtils.copyDirectory(SINGLE_APP_WITH_NO_SDK_THIRDPARTY, tempDir);
		initModel(tempDir);
		CheckCommand checkCommand = new CheckCommand();
		
		assertTrue(new File(tempDir, APPLICATIONS_DIR + "/firstapp/thirdparty-libraries/jQuery").exists());
		assertTrue(new File(tempDir, APPLICATIONS_DIR + "/firstapp/thirdparty-libraries/Sencha").exists());
		
		checkCommand.doCommand( new String[] {""} );
		
		
		logStore.verifyConsoleLogMessages("There are no thirdparty libraries present in the SDK.");		
	}

	private void initModel(File brjsDir) throws InvalidSdkDirectoryException
	{
		BRJS brjs = createModel(brjsDir, new TestLoggerFactory(logStore));
		ThreadSafeStaticBRJSAccessor.destroy();
		ThreadSafeStaticBRJSAccessor.initializeModel(brjs);
	}
	
	
}
