package com.caplin.cutlass.command.create;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.util.FileUtility;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.plugins.commands.standard.CreateApplicationCommand;
import org.bladerunnerjs.utility.ServerUtility;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

public class CreateApplicationCommandTest
{
	private static final File testResourcesSdkDir = new File("src/test/resources/CreateBladeCommand");

	private BRJS brjs;
	private File sdkBaseDir;
	
	private CreateApplicationCommand createApplicationCommand;
	
	private File newApplicationToBeCreatedDirectory;
	
	@Before
	public void setup() throws Exception
	{
		File tempDirRoot = FileUtility.createTemporaryDirectory(this.getClass().getSimpleName());
		FileUtils.copyDirectory(testResourcesSdkDir, tempDirRoot);
		brjs = BRJSAccessor.initialize(BRJSTestFactory.createBRJS(tempDirRoot));
		
		//TODO: this should probably use the utility method in ApplicationServer (or another class)
		brjs.bladerunnerConf().setJettyPort( ServerUtility.getTestPort() );
		
		sdkBaseDir = new File(tempDirRoot, CutlassConfig.SDK_DIR);
		createApplicationCommand = new CreateApplicationCommand();
		createApplicationCommand.setBRJS(brjs);
		
		newApplicationToBeCreatedDirectory = new File(sdkBaseDir.getParentFile(), CutlassConfig.APPLICATIONS_DIR + "/newtrader");
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfApplicationAlreadyExists() throws Exception
	{
		createApplicationCommand.doCommand(new String[] { "fxtrader", "novox" });
	}
	
	@Test
	public void commandCopiesOverTemplateApplication() throws Exception
	{
		File applicationSourceFile = new File(newApplicationToBeCreatedDirectory, "default-aspect/src/novox/App.js");
		assertFalse(newApplicationToBeCreatedDirectory.exists());
		assertFalse(applicationSourceFile.exists());
		
		createApplicationCommand.doCommand(new String[] { "newtrader", "novox" });
		
		assertTrue(newApplicationToBeCreatedDirectory.exists());
		assertTrue(applicationSourceFile.exists());
		
		String oldContent = "appns.App = function()";
		String newContent = "novox.App = function()";
		
		List<String> content = FileUtils.readLines(applicationSourceFile);
		assertFalse(content.contains(oldContent));
		assertTrue(content.contains(newContent));
		
		content = FileUtils.readLines(new File(newApplicationToBeCreatedDirectory, "WEB-INF/jetty-env.xml"));
		assertTrue(content.contains("				<Set name=\"jdbcUrl\">jdbc:h2:../webcentric-db/newtrader/newtrader;IFEXISTS=TRUE;AUTO_SERVER=TRUE</Set>"));
		assertTrue(new File(newApplicationToBeCreatedDirectory, "thirdparty-libraries").exists() == true);
	}

	@Test
	public void commandCreatesCorrectAppConf() throws Exception
	{
		File appConf = new File(newApplicationToBeCreatedDirectory, "app.conf");
		
		createApplicationCommand.doCommand(new String[] { "newtrader", "namespacex" });
		
		assertTrue(newApplicationToBeCreatedDirectory.exists());
		assertTrue(appConf.exists());
		
		List<String> appConfLines = FileUtils.readLines(appConf);
		assertEquals("requirePrefix: namespacex", appConfLines.get(1));
	}

	@Test
	public void appnamespaceIsAutomaticallyGeneratedIfNotProvided() throws Exception
	{
		File appConf = new File(newApplicationToBeCreatedDirectory, "app.conf");
		
		createApplicationCommand.doCommand(new String[] { "newtrader" });
		
		assertTrue(newApplicationToBeCreatedDirectory.exists());
		assertTrue(appConf.exists());
		
		List<String> appConfLines = FileUtils.readLines(appConf);
		assertEquals("requirePrefix: newtrader", appConfLines.get(1));
	}
	
	@Test
	public void testAutoDeployFileIsNotCreatedAfterCreationIfAppServerNotRunning() throws Exception
	{
		createApplicationCommand.doCommand(new String[] { "newtrader", "novox" });
		assertFalse( new File(newApplicationToBeCreatedDirectory, CutlassConfig.AUTO_DEPLOY_CONTEXT_FILENAME).exists() );		
	}
	
	@Test
	public void commandPopulatesWebInfLibWithSdkJars() throws Exception
	{
		createApplicationCommand.doCommand(new String[] { "newtrader", "novox" });
		assertTrue(new File(newApplicationToBeCreatedDirectory, "WEB-INF/lib/brjs-test.jar").exists());
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandDoesNotAllowReservedNamespace() throws Exception
	{
		createApplicationCommand.doCommand(new String[] { "novotrader", "caplin" });
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandDoesNotAllowJavaScriptKeywordAsNamespace() throws Exception
	{
		createApplicationCommand.doCommand(new String[] { "novotrader", "default" });
	}
}
