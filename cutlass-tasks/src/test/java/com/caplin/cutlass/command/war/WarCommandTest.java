package com.caplin.cutlass.command.war;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.BRJS;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.plugin.plugins.commands.standard.WarCommand;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.util.FileUtility;
import com.caplin.cutlass.command.CommandTaskTest;

public class WarCommandTest extends CommandTaskTest
{
	private File tempDirectory;
	private static File warFile;
	private File testResourceDir = new File("src/test/resources/WarCommandUtilityTest");
	private WarCommand warCommand;
	
	@Before
	public void setUp() throws IOException
	{
		File tempDir = FileUtility.createTemporaryDirectory(getClass().getName());
		FileUtils.copyDirectory(testResourceDir, tempDir);
		BRJS brjs = BRJSAccessor.initialize(BRJSTestFactory.createBRJS(tempDir));
		out = BRJSAccessor.root.getConsoleWriter();
		warCommand = new WarCommand();
		warCommand.setBRJS(brjs);
		
		tempDirectory = FileUtility.createTemporaryDirectory("warCommandTest");
		warFile = new File(tempDirectory, "tempWar.war");
	}

	@After
	public void tearDownClass()
	{
		warFile.delete();
		tempDirectory.delete();
	}

	@Test
	public void testWarCommandForApplicationThatExistsAndWarOutputSpecifiedAndVerifyWarContents() throws Exception
	{
		assertFalse(warFile.exists());

		warCommand.doCommand(new String[] { "emptytrader", warFile.getAbsolutePath() });

		assertTrue(warFile.exists());
		
		File extractLocation = FileUtility.createTemporaryDirectory("warContent");
		
		FileUtility.unzip(new ZipFile(warFile), extractLocation);
		assertTrue(new File(extractLocation, "login-aspect").exists());
		assertTrue(new File(extractLocation, "default-aspect").exists());
		assertTrue(new File(extractLocation, "mobile-aspect").exists());
		assertTrue(new File(extractLocation, "WEB-INF").exists());
		assertTrue(new File(extractLocation, "WEB-INF/lib").exists());
		assertTrue(new File(extractLocation, "WEB-INF/web.xml").exists());
		assertTrue(new File(extractLocation, "app.conf").exists());
	}
	
	@Test
	public void testWarCommandForApplicationWithHiddenFolders() throws Exception
	{
		// Not run on windows because creating hidden folders on Windows without Java 7 is unreliable
		if (!System.getProperty("os.name").toLowerCase().contains("windows"))
		{
			assertFalse(warFile.exists());
			
			File copiedTestResources = new File(tempDirectory, "copiedResources");
			FileUtility.copyDirectoryContents(new File("src/test/resources/WarCommandUtilityTest"), copiedTestResources);
			
			File copiedSdkBaseDir = new File(copiedTestResources, CutlassConfig.SDK_DIR);
			BRJS brjs = BRJSAccessor.initialize(BRJSTestFactory.createBRJS(copiedSdkBaseDir));
			WarCommand newWarCommand = new WarCommand();
			newWarCommand.setBRJS(brjs);
			
			createHiddenFoldersInsideAppStructure(copiedTestResources);

			newWarCommand.doCommand(new String[] { "emptytrader", warFile.getAbsolutePath() });
	
			assertTrue(warFile.exists());
			
			File extractLocation = FileUtility.createTemporaryDirectory("warContent");
			
			FileUtility.unzip(new ZipFile(warFile), extractLocation);
			assertTrue(new File(extractLocation, "login-aspect").exists());
			assertTrue(new File(extractLocation, "default-aspect").exists());
			assertTrue(new File(extractLocation, "mobile-aspect").exists());
			assertTrue(new File(extractLocation, "WEB-INF").exists());
			assertTrue(new File(extractLocation, "WEB-INF/lib").exists());
			assertTrue(new File(extractLocation, "WEB-INF/web.xml").exists());
			assertTrue(new File(extractLocation, "app.conf").exists());
		}
	}

	private void createHiddenFoldersInsideAppStructure(File rootDir) 
	{
		new File(rootDir, "apps/emptytrader/bs1-bladeset/resources/.svn").mkdirs();
		new File(rootDir, "apps/emptytrader/bs1-bladeset/blades/b1/resources/.svn").mkdirs();
		new File(rootDir, "apps/emptytrader/bs1-bladeset/blades/b1/resources/html/.svn").mkdirs();
		new File(rootDir, "apps/emptytrader/bs2-bladeset/blades/b2/resources/i18n/.svn").mkdirs();
		new File(rootDir, "apps/emptytrader/bs2-bladeset/blades/b2/resources/i18n/en/.svn").mkdirs();
		new File(rootDir, "apps/emptytrader/bs2-bladeset/blades/b2/resources/xml/.svn").mkdirs();
		new File(rootDir, "apps/emptytrader/bs2-bladeset/blades/b2/themes/.svn").mkdirs();
		new File(rootDir, "apps/emptytrader/bs2-bladeset/blades/b2/themes/noir/.svn").mkdirs();
		new File(rootDir, "apps/emptytrader/default-aspect/.svn").mkdirs();
		new File(rootDir, "apps/emptytrader/default-aspect/themes/.svn").mkdirs();
		new File(rootDir, "apps/emptytrader/default-aspect/unbundled-resources/.svn").mkdirs();
		new File(rootDir, "apps/emptytrader/default-aspect/unbundled-resources/subfolder/.svn").mkdirs();
		new File(rootDir, "apps/emptytrader/thirdparty-libraries/.svn").mkdirs();
		new File(rootDir, "apps/emptytrader/thirdparty-libraries/subfolder/.svn").mkdirs();
	}
	
	@Test
	public void testWarCommandForApplicationThatExistsWithoutWarOutputSpecified() throws Exception
	{
		warFile = new File("emptytrader.war");
		assertFalse(warFile.exists());

		warCommand.doCommand(new String[] { "emptytrader" });

		assertTrue(warFile.exists());
	}
	
	@Test
	public void testWarCommandWithoutAddingDotWarSuffix() throws Exception
	{
		warFile = new File(tempDirectory, "emptytrader");
		assertFalse(warFile.exists());

		warCommand.doCommand(new String[] { "emptytrader", warFile.getAbsolutePath() });

		assertFalse(warFile.exists());
		assertTrue(new File(tempDirectory, "emptytrader.war").exists());
	}
	
	@Test
	public void testWarCommandForApplicationWhenPassingInDirectoryAsOutputLocation() throws Exception
	{
		warFile = new File(tempDirectory, "emptytrader.war");
		
		assertFalse(warFile.exists());

		warCommand.doCommand(new String[] { "emptytrader", warFile.getParentFile().getAbsolutePath() });

		assertTrue(warFile.exists());
	}

	@Test(expected = CommandArgumentsException.class)
	public void testWarCommandForApplicationThatDoesNotExist() throws Exception
	{
		warCommand.doCommand(new String[] { "doesnotexist", tempDirectory + "tempWar.war" });
	}
}
