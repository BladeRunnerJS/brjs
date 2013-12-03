package com.caplin.cutlass.command.importing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.bladerunnerjs.model.BRJS;
import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.sinbin.CutlassConfig;
import com.caplin.cutlass.util.FileUtility;

public class ImportApplicationCommandTest
{
	
	private static final File TEST_BASE = new File("src/test/resources/ImportApplicationCommandUtility");
	private File sdkBaseDir, applicationsDirectory, tempDir;
	private ImportApplicationCommand importApplicationCommand;
	private BRJS brjs;
	
	@Before
	public void setUp() throws IOException, ConfigException
	{
		tempDir = FileUtility.createTemporaryDirectory("ImportCommandTest");
		FileUtility.copyDirectoryContents(TEST_BASE, tempDir);
		
		sdkBaseDir = new File(tempDir, "sdk-only/"+CutlassConfig.SDK_DIR);
		brjs = BRJSTestFactory.createBRJS( sdkBaseDir.getParentFile() );
		BRJSAccessor.initialize( brjs );
		
		applicationsDirectory = new File(sdkBaseDir.getParent(), CutlassConfig.APPLICATIONS_DIR);
		
		importApplicationCommand = new ImportApplicationCommand(brjs);
		tempDir.deleteOnExit();
		
	}

	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfIncorrectNumberArgumentsArePassedIn() throws Exception
	{
		importApplicationCommand.doCommand(new String[] { "emptytrader.zip" });
	}

	@Ignore //TODO: this test should be in the CT3 plugin
	@Test
	public void importsZippedWebcentricApplicationAndCreatesDatabase() throws Exception
	{
//		File appDirectory = new File(applicationsDirectory, "novotrader");
//		File emptyTraderWEBINFJar = new File(appDirectory, "WEB-INF/lib/testJar.jar");
//		File generatedWebcentricFolder = new File(sdkBaseDir.getParent(), CutlassConfig.WEBCENTRIC_DB_DIR);
//		
//		assertFalse(appDirectory.exists());
//		assertFalse(emptyTraderWEBINFJar.exists());
//		assertFalse(generatedWebcentricFolder.exists());
//
//		importApplicationCommand.doCommand(new String[] { "emptytrader.zip", "novotrader", "novox" });
//
//		assertTrue(appDirectory.exists());
//		assertTrue(emptyTraderWEBINFJar.exists());
//		assertTrue(new File(generatedWebcentricFolder, "novotrader").exists());
	}

	@Ignore //TODO: this test should be in the CT3 plugin - it also doesnt really test anything since the webcentric db dir doesnt live in SDK
	@Test 
	public void importsZippedNonWebcentricApplicationAndDoesNotCreateDatabase() throws Exception
	{
//		File appDirectory = new File(applicationsDirectory, "bluetrader");
//		File generatedWebcentricFolder = new File(sdkBaseDir.getParent(), CutlassConfig.WEBCENTRIC_DB_DIR);
//		
//		assertFalse(appDirectory.exists());
//		assertFalse(generatedWebcentricFolder.exists());
//		
//		importApplicationCommand.doCommand(new String[] { "plaintrader.zip", "bluetrader", "novox" });
//
//		assertTrue(appDirectory.exists());
//		assertFalse(generatedWebcentricFolder.exists());
	}
	
	@Test
	public void copiesAcrossAppConfFile() throws Exception
	{
		File appDirectory = new File(applicationsDirectory, "newtrader");
		File appConf = new File(appDirectory, "app.conf");
		
		importApplicationCommand.doCommand(new String[] { "plaintrader.zip", "newtrader", "novox" });

		assertTrue(appDirectory.exists());
		assertTrue(appConf.exists());
		
		List<String> appConfLines = FileUtils.readLines(appConf);
		assertEquals("appNamespace: novox", appConfLines.get(0));
	}

	// <app-name> exception test cases
	@Test (expected=CommandArgumentsException.class)
	public void importsZippedAppToANewAppNameWithSpecialCharsThrowsException() throws Exception
	{	
		importApplicationCommand.doCommand(new String[] { "emptytrader.zip", "cr�dit", "novox" });
	}

	@Test
	public void importsZippedAppToANewAppNameWithANumber() throws Exception
	{
		importApplicationCommand.doCommand(new String[] { "emptytrader.zip", "novotrader2", "novox" });
	}

	@Test
	public void importsZippedAppToANewAppNameWithAnUnderscore() throws Exception
	{
		importApplicationCommand.doCommand(new String[] { "emptytrader.zip", "novotrader_new", "novox" });
	}

	@Test
	public void importsZippedAppToANewAppNameWithAHyphen() throws Exception
	{
		importApplicationCommand.doCommand(new String[] { "emptytrader.zip", "novo-trader", "novox" });
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void importsZippedAppToANewAppNameWithSpacesThrowsException() throws Exception
	{
		importApplicationCommand.doCommand(new String[] { "emptytrader.zip", "novo trader", "novox" });
	}

	// <app-namespace> exception test cases 
	@Test (expected=CommandArgumentsException.class)
	public void importsZippedAppToANewNamespaceWithSpecialCharsThrowsException() throws Exception
	{
		importApplicationCommand.doCommand(new String[] { "emptytrader.zip", "novotrader", "novo�" });
	}

	@Test
	public void importsZippedAppToANewNamespaceWithANumberIsAllowed() throws Exception
	{
		importApplicationCommand.doCommand(new String[] { "emptytrader.zip", "novotrader", "novox2" });
	}

	@Test (expected=CommandArgumentsException.class)
	public void importsZippedAppToANewNamespaceWithAnUnderscoreThrowsException() throws Exception
	{
		importApplicationCommand.doCommand(new String[] { "emptytrader.zip", "novotrader", "nov_ox" });
	}

	@Test (expected=CommandArgumentsException.class)
	public void importsZippedAppToANewNamespaceWithAHyphenThrowsException() throws Exception
	{
		importApplicationCommand.doCommand(new String[] { "emptytrader.zip", "novotrader", "nov-ox" });
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void importsZippedAppToANewNamespaceWithSpacesThrowsException() throws Exception
	{
		importApplicationCommand.doCommand(new String[] { "emptytrader.zip", "novotrader", "nov ox" });
	}

	@Test (expected=CommandArgumentsException.class)
	public void importsZippedAppToANewNamespaceWithReservedCaplinNamespaceThrowsException() throws Exception
	{
		importApplicationCommand.doCommand(new String[] { "emptytrader.zip", "novotrader", "caplin" });
	}

	@Test 
	public void zippedApplicationWithParentFolderHasSpacesCanBeImported() throws Exception
	{
		sdkBaseDir = new File(tempDir, "folder with spaces/" + CutlassConfig.SDK_DIR);
		brjs = BRJSTestFactory.createBRJS(sdkBaseDir);
		
		File applicationsDirectory = new File(sdkBaseDir.getParent(), CutlassConfig.APPLICATIONS_DIR);
		File appDirectory = new File(applicationsDirectory, "novotrader");
		importApplicationCommand = new ImportApplicationCommand(brjs);

		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(sdkBaseDir));
		
		assertFalse(appDirectory.exists());

		importApplicationCommand.doCommand(new String[] { "plaintrader.zip", "novotrader", "novox" });

		assertTrue(appDirectory.exists());
	}

	@Test (expected=CommandArgumentsException.class)
	public void importZippedApplicationPassingTooManyParameters() throws Exception
	{
		importApplicationCommand.doCommand(new String[] { "emptytrader.zip", "novotrader", "novox", "novox2" });
	}

	@Test (expected=CommandArgumentsException.class)
	public void importZippedApplicationPassingTooFewParameters() throws Exception
	{
		importApplicationCommand.doCommand(new String[] { "emptytrader.zip", "novotrader" });
	}

	@Test (expected=CommandOperationException.class)
	public void importZippedApplicationPassingInAnInvalidZipFileLocation() throws Exception
	{	
		importApplicationCommand.doCommand(new String[] { "doesnotexist.zip", "novotrader", "novox" });
	}

	@Test (expected=CommandOperationException.class)
	public void importZippedApplicationUsingAnAppNameThatAlreadyExists() throws Exception
	{
		File appDirectory = new File(applicationsDirectory, "novotrader");
		assertFalse(applicationsDirectory.exists());		
		
		applicationsDirectory.mkdir();
		appDirectory.mkdir();
		
		importApplicationCommand.doCommand(new String[] { "emptytrader.zip", "novotrader", "novox" });
	}
}
