package com.caplin.cutlass.command.importing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

import org.bladerunnerjs.model.sinbin.CutlassConfig;
import org.bladerunnerjs.model.utility.FileUtility;

public class RenamerTest
{
	private static final File TEST_BASE = new File("src/test/resources/Renamer");
	private File appsDir;
	private File applicationDir;

	@Before
	public void setup() throws IOException
	{
		appsDir = createTempAppsDir(new File(TEST_BASE, CutlassConfig.APPLICATIONS_DIR));
		applicationDir = new File(appsDir, "emptytrader");
		
		BRJSAccessor.initialize( BRJSTestFactory.createBRJS(appsDir.getParentFile()) );
	}
	
	@Test
	public void testMoveNamespaceIsCorrect() throws Exception
	{
		File originalFile = new File(applicationDir, "example-bladeset/blades/grid/src/emptycorp/example/grid/RightClickMenuDecorator.js");
		File expectedRenamedFile = new File(applicationDir, "example-bladeset/blades/grid/src/mybank/example/grid/RightClickMenuDecorator.js");
		assertFileExists(originalFile);
		assertFileDoesNotExist(expectedRenamedFile);
		
		Renamer.renameApplication(applicationDir, "emptycorp", "mybank", "emptytrader", "emptytrader");
		
		assertFileExists(expectedRenamedFile);
		assertFileDoesNotExist(originalFile);
	}
	
	@Test
	public void testRenameAppIsCorrect() throws Exception
	{
		File originalFile = new File(appsDir, "emptytrader/main-aspect/src/emptycorp/emptytrader/EmptyTraderApp.js");
		File expectedRenamedFile = new File(appsDir, "acmetrader/main-aspect/src/acmebank/emptytrader/EmptyTraderApp.js");
		assertTrue("Expected " + originalFile + " to exist before test", originalFile.exists());
		assertFalse("Expected " + expectedRenamedFile + " to not exist before test", expectedRenamedFile.exists());
				
		Renamer.renameApplication(applicationDir, "emptycorp", "acmebank", "emptytrader","acmetrader");
		
		List<String> renamedAppConfContents = IOUtils.readLines(new FileInputStream(new File(appsDir, "acmetrader/app.conf")));
		
		assertTrue("Expected " + expectedRenamedFile + " to exist", expectedRenamedFile.exists());
		assertFalse("Expected " + originalFile + " to not exist", originalFile.exists());
		assertEquals(Arrays.asList("appNamespace: acmebank","locales: en"), renamedAppConfContents);
	}
	
	@Test
	public void testRenameAppToSameAppNameAndNamespace() throws Exception
	{		
		File originalFile = new File(appsDir, "emptytrader/main-aspect/src/emptycorp/emptytrader/EmptyTraderApp.js");
		File expectedRenamedFile = new File(appsDir, "acmetrader/main-aspect/src/emptycorp/emptytrader/EmptyTraderApp.js");
		assertTrue("Expected " + originalFile + " to exist before test", originalFile.exists());
		assertFalse("Expected " + expectedRenamedFile + " to not exist before test", expectedRenamedFile.exists());
		
		FileInputStream emptytraderConf = new FileInputStream(new File(appsDir, "emptytrader/app.conf"));
		
		List<String> originalAppConfContents = IOUtils.readLines(emptytraderConf);
		emptytraderConf.close();
		
		Renamer.renameApplication(applicationDir, "emptycorp", "emptycorp", "emptytrader","acmetrader");
		
		FileInputStream acmetraderConf = new FileInputStream(new File(appsDir, "acmetrader/app.conf"));
		List<String> renamedAppConfContents = IOUtils.readLines(acmetraderConf);
		
		assertTrue("Expected " + expectedRenamedFile + " to exist", expectedRenamedFile.exists());
		assertFalse("Expected " + originalFile + " to not exist", originalFile.exists());
		assertEquals(originalAppConfContents, renamedAppConfContents);
	}
		
	@Test
	public void testFindAndReplaceChangesInternalNamespaceForBlade() throws Exception
	{
		File originalFile = new File(applicationDir, "example-bladeset/blades/grid/src/emptycorp/example/grid/RightClickMenuDecorator.js");
		File expectedRenamedFile = new File(applicationDir, "example-bladeset/blades/grid/src/novox/example/grid/RightClickMenuDecorator.js");
		String oldDeclaration = "caplin.namespace(\"emptycorp.example.grid\");";
		String newDeclaration = "caplin.namespace(\"novox.example.grid\");";
		
		String content = FileUtils.readFileToString(originalFile);
		assertFalse(content.contains(newDeclaration));
		assertTrue(content.contains(oldDeclaration));
		
		Renamer.renameApplication(applicationDir, "emptycorp", "novox", "emptytrader", "emptytrader");
		
		content = FileUtils.readFileToString(expectedRenamedFile);
		assertTrue(content.contains(newDeclaration));
		assertFalse(content.contains(oldDeclaration));
	}
	
	@Test
	public void testFindAndReplaceChangesInternalNamespaceForMainApp() throws Exception
	{
		File originalFile = new File(appsDir, "emptytrader/main-aspect/src/emptycorp/emptytrader/EmptyTraderApp.js");
		File expectedRenamedFile = new File(appsDir, "myapp/main-aspect/src/novox/emptytrader/EmptyTraderApp.js");
		String oldDeclaration = "caplin.namespace(\"emptycorp.emptytrader\");";
		String newDeclaration = "caplin.namespace(\"novox.emptytrader\");";
		
		String content = FileUtils.readFileToString(originalFile);
		assertFalse(content.contains(newDeclaration));
		assertTrue(content.contains(oldDeclaration));
		
		Renamer.renameApplication(applicationDir, "emptycorp", "novox", "emptytrader", "myapp");
		
		content = FileUtils.readFileToString(expectedRenamedFile);
		assertTrue(content, content.contains(newDeclaration));
		assertFalse(content, content.contains(oldDeclaration));
	}
	 
	@Test
	public void testFindAndReplaceChangesInternalNamespaceForAppWithCommonCharNamespace() throws Exception
	{
		File originalAppDir = new File(appsDir, "slimshady");
		File renamedAppDir = new File(appsDir, "novotrader");
		
		File originalFile = new File(originalAppDir, "default-aspect/index.html");
		File expectedRenamedFile = new File(renamedAppDir, "default-aspect/index.html");
		
		String oldDeclaration = "var oApp = new ss.slimshady.SlimShadyApp();";
		String newDeclaration = "var oApp = new novox.slimshady.SlimShadyApp();";
		
		String originalContent = FileUtils.readFileToString(originalFile);
		assertTrue(originalContent.contains(oldDeclaration));
		assertTrue(originalContent, originalContent.contains("@css.bundle@"));
		assertFalse(originalContent.contains(newDeclaration));
		
		Renamer.renameApplication(originalAppDir, "ss", "novox", "sstrader", "novotrader");
		String renamedContent = FileUtils.readFileToString(expectedRenamedFile);
		
		assertTrue(renamedContent, renamedContent.contains(newDeclaration));
		assertTrue(renamedContent, renamedContent.contains("@css.bundle@"));
		assertFalse(renamedContent, renamedContent.contains(oldDeclaration));
	}
	
	@Test
	public void testFindAndReplaceChangesInternalNamespaceForJspApp() throws Exception
	{
		File originalAppDir = new File(appsDir, "jspapp");
		File renamedAppDir = new File(appsDir, "novotrader");
		
		File originalFile = new File(originalAppDir, "default-aspect/index.jsp");
		File expectedRenamedFile = new File(renamedAppDir, "default-aspect/index.jsp");
		
		String oldDeclaration = "var oApp = new jspapp.ShinyApp();";
		String newDeclaration = "var oApp = new novox.ShinyApp();";
		
		String originalContent = FileUtils.readFileToString(originalFile);
		assertTrue(originalContent.contains(oldDeclaration));
		assertFalse(originalContent.contains(newDeclaration));
		
		Renamer.renameApplication(originalAppDir, "jspapp", "novox", "jspapp", "novotrader");
		String renamedContent = FileUtils.readFileToString(expectedRenamedFile);
		
		assertTrue(renamedContent, renamedContent.contains(newDeclaration));
		assertFalse(renamedContent, renamedContent.contains(oldDeclaration));
	}

	@Test
	public void testFindAndReplaceI18nStringsAtTheStartOfFile() throws Exception
	{
		File originalAppDir = new File(appsDir, "slimshady");
		File renamedAppDir = new File(appsDir, "novotrader");
		
		File originalFile = new File(originalAppDir, "default-aspect/resources/i18n/en/en.properties");
		File expectedRenamedFile = new File(renamedAppDir, "default-aspect/resources/i18n/en/en.properties");
		
		String oldDeclaration1 = "ss.fx.bladesetname = fx";
		String oldDeclaration2 = "ss.fx.randomtoken = randomtoken";
		String newDeclaration1 = "novox.fx.bladesetname = fx";
		String newDeclaration2 = "novox.fx.randomtoken = randomtoken";
		
		String originalContent = FileUtils.readFileToString(originalFile);
		assertTrue(originalContent.contains(oldDeclaration1));
		assertTrue(originalContent.contains(oldDeclaration2));
		
		Renamer.renameApplication(originalAppDir, "ss", "novox", "sstrader", "novotrader");
		String renamedContent = FileUtils.readFileToString(expectedRenamedFile);
		
		assertTrue(renamedContent, renamedContent.contains(newDeclaration1));
		assertTrue(renamedContent, renamedContent.contains(newDeclaration2));
	}
	
	@Test
	public void testFindAndReplaceChangesDatabaseUrl() throws Exception
	{
		File originalFile = new File(appsDir, "emptytrader/WEB-INF/jetty-env.xml");
		File expectedRenamedFile = new File(appsDir, "myapp/WEB-INF/jetty-env.xml");
		String oldDeclaration = "../webcentric-db/emptytrader/emptytrader";
		String newDeclaration = "../webcentric-db/myapp/myapp";
		
		String content = FileUtils.readFileToString(originalFile);
		assertFalse(content.contains(newDeclaration));
		assertTrue(content.contains(oldDeclaration));
		
		Renamer.renameApplication(applicationDir, "emptycorp", "novox", "emptytrader", "myapp");
		
		content = FileUtils.readFileToString(expectedRenamedFile);
		assertTrue(content, content.contains(newDeclaration));
		assertFalse(content, content.contains(oldDeclaration));
	}
	
	@Test
	public void testRenameAspectIsCorrect() throws Exception
	{
		File originalAspectDir = new File(applicationDir, "main-aspect");
		File originalAspectSrcFile = new File(applicationDir, "main-aspect/src/emptycorp");
		File renamedAspectSrcFile = new File(applicationDir, "main-aspect/src/new");
		assertFileExists(originalAspectSrcFile);
		assertFileDoesNotExist(renamedAspectSrcFile);
		
		Renamer.renameAspect(originalAspectDir, "emptycorp", "new");
		
		assertFileDoesNotExist(originalAspectSrcFile);
		assertFileExists(renamedAspectSrcFile);
	}
	
	@Test
	public void testRenameBladesetIsCorrect() throws Exception
	{
		File originalFile = new File(applicationDir, "example-bladeset/blades/grid/src/emptycorp/example/grid/RightClickMenuDecorator.js");
		File expectedRenamedFile = new File(applicationDir, "limitorders-bladeset/blades/grid/src/emptycorp/limitorders/grid/RightClickMenuDecorator.js");
		assertFileExists(originalFile);
		assertFileDoesNotExist(expectedRenamedFile);
		
		Renamer.renameBladeset(new File(applicationDir, "example-bladeset"), "emptycorp.example", "emptycorp.limitorders");
		
		assertFileExists(expectedRenamedFile);
		assertFileDoesNotExist(originalFile);
		
		String newAliasDefContents = FileUtils.readFileToString(new File(applicationDir, "limitorders-bladeset/resources/aliasDefinitions.xml"));
		assertFalse(newAliasDefContents.contains("emptycorp.example"));
		assertTrue(newAliasDefContents.contains("emptycorpsexamples.InputControl"));
		assertTrue(newAliasDefContents.contains("emptycorp.limitorders"));
	}
	
	@Test
	public void testRenameBladesetToDifferentAppNameSpaceIsCorrect() throws Exception
	{
		File originalFile = new File(applicationDir, "example-bladeset/blades/grid/src/emptycorp/example/grid/RightClickMenuDecorator.js");
		File expectedRenamedFile = new File(applicationDir, "limitorders-bladeset/blades/grid/src/newcorp/limitorders/grid/RightClickMenuDecorator.js");
		assertFileExists(originalFile);
		assertFileDoesNotExist(expectedRenamedFile);
		
		Renamer.renameBladeset(new File(applicationDir, "example-bladeset"), "emptycorp.example", "newcorp.limitorders");
		
		assertFileExists(expectedRenamedFile);
		assertFileDoesNotExist(originalFile);
		assertFileDoesNotExist(new File(applicationDir, "limitorders-bladeset/blades/grid/src/emptycorp"));
		
		String newAliasDefContents = FileUtils.readFileToString(new File(applicationDir, "limitorders-bladeset/resources/aliasDefinitions.xml"));
		assertFalse(newAliasDefContents.contains("emptycorp.example"));
		assertTrue(newAliasDefContents.contains("emptycorpsexamples.InputControl"));
		assertTrue(newAliasDefContents.contains("newcorp.limitorders"));
	}
	
	@Test
	public void testRenameBladesetIsCorrectForBladesetNamedWithCommonCharacters() throws Exception
	{
		File originalFile = new File(applicationDir, "a-bladeset/blades/grid/src/emptycorp/a/grid/RightClickMenuDecorator.js");
		File expectedRenamedFile = new File(applicationDir, "fx-bladeset/blades/grid/src/emptycorp/fx/grid/RightClickMenuDecorator.js");
		assertFileExists(originalFile);
		assertFileDoesNotExist(expectedRenamedFile);
		
		Renamer.renameBladeset(new File(applicationDir, "a-bladeset"), "emptycorp.a", "emptycorp.fx");
		
		assertFileExists(expectedRenamedFile);
		assertFileDoesNotExist(originalFile);
		assertFileDoesNotExist(new File(applicationDir, "fx-bladeset/blades/grid/src/emptycorp/fx/a"));
	}
	
	@Test
	public void testRenameFileNamesReplacesNestedMatches() throws Exception
	{
		File originalFile = new File(appsDir, "emptytrader/main-aspect/src/emptycorp/emptytrader");
		File expectedRenamedFile = new File(appsDir, "app/main-aspect/src/bank/emptytrader");
		assertFileExists(originalFile);
		assertFileDoesNotExist(expectedRenamedFile);
		
		Renamer.renameApplication(applicationDir, "emptycorp", "bank", "emptytrader", "app");
		
		assertFileExists(expectedRenamedFile);
		assertFileDoesNotExist(originalFile);
	}
	
	@Test
	public void testRenameBladeChangesDirectoryStructure() throws Exception
	{
		File originalFile = new File(applicationDir, "example-bladeset/blades/grid/src/emptycorp/example/grid/RightClickMenuDecorator.js");
		File expectedRenamedFile = new File(applicationDir, "example-bladeset/blades/newblade/src/emptycorp/example/newblade/RightClickMenuDecorator.js");
		assertFileExists(originalFile);
		assertFileDoesNotExist(expectedRenamedFile);
		
		Renamer.renameBlade(new File(applicationDir, "example-bladeset/blades/grid"), "emptycorp.example.grid", "emptycorp.example.newblade");
		
		assertFileExists(expectedRenamedFile);
		assertFileDoesNotExist(originalFile);
	}
	
	@Test
	public void testRenameBladeChangesFileContent() throws Exception
	{
		File originalFile = new File(applicationDir, "example-bladeset/blades/grid/src/emptycorp/example/grid/RightClickMenuDecorator.js");
		File expectedRenamedFile = new File(applicationDir, "example-bladeset/blades/mygrid/src/emptycorp/example/mygrid/RightClickMenuDecorator.js");
		String oldDeclaration = "caplin.namespace(\"emptycorp.example.grid\");";
		String newDeclaration = "caplin.namespace(\"emptycorp.example.mygrid\");";
		
		String content = FileUtils.readFileToString(originalFile);
		assertFalse(content.contains(newDeclaration));
		assertTrue(content.contains(oldDeclaration));
		
		Renamer.renameBlade(new File(applicationDir, "example-bladeset/blades/grid"), "emptycorp.example.grid", "emptycorp.example.mygrid");
		
		content = FileUtils.readFileToString(expectedRenamedFile);
		assertTrue(content, content.contains(newDeclaration));
		assertFalse(content, content.contains(oldDeclaration));
	}
	
	@Test
	public void testRenameBladeChangesFileContentAndBladesetNamespace() throws Exception
	{
		File originalFile = new File(applicationDir, "example-bladeset/blades/grid/src/emptycorp/example/grid/RightClickMenuDecorator.js");
		File expectedRenamedFile = new File(applicationDir, "example-bladeset/blades/mygrid/src/emptycorp/fx/mygrid/RightClickMenuDecorator.js");
		String oldDeclaration = "caplin.namespace(\"emptycorp.example.grid\");";
		String newDeclaration = "caplin.namespace(\"emptycorp.fx.mygrid\");";
		
		String content = FileUtils.readFileToString(originalFile);
		assertFalse(content.contains(newDeclaration));
		assertTrue(content.contains(oldDeclaration));
		
		Renamer.renameBlade(new File(applicationDir, "example-bladeset/blades/grid"), "emptycorp.example.grid", "emptycorp.fx.mygrid");
		
		content = FileUtils.readFileToString(expectedRenamedFile);
		assertTrue(content, content.contains(newDeclaration));
		assertFalse(content, content.contains(oldDeclaration));
	}
	
	@Test
	public void testRenameBladeChangesFileContentAndApplicationNamespace() throws Exception
	{
		File originalApplicationNamespaceDirectory = new File(applicationDir, "example-bladeset/blades/grid/src/emptycorp/");
		File originalFile = new File(applicationDir, "example-bladeset/blades/grid/src/emptycorp/example/grid/RightClickMenuDecorator.js");
		File expectedRenamedFile = new File(applicationDir, "example-bladeset/blades/grid/src/caplinx/example/grid/RightClickMenuDecorator.js");
		String oldDeclaration = "caplin.namespace(\"emptycorp.example.grid\");";
		String newDeclaration = "caplin.namespace(\"caplinx.example.grid\");";
		
		String content = FileUtils.readFileToString(originalFile);
		assertFalse(content.contains(newDeclaration));
		assertTrue(content.contains(oldDeclaration));
		assertTrue(originalApplicationNamespaceDirectory.exists());
		
		Renamer.renameBlade(new File(applicationDir, "example-bladeset/blades/grid"), "emptycorp.example.grid", "caplinx.example.grid");
		
		content = FileUtils.readFileToString(expectedRenamedFile);
		assertTrue(content, content.contains(newDeclaration));
		assertFalse(content, content.contains(oldDeclaration));
		assertFalse(originalApplicationNamespaceDirectory.exists());
	}
	
	@Test
	public void testRenameBladeChangesTestFileContentAndApplicationNamespace() throws Exception
	{
		File jstestDriverConf = new File(applicationDir, "a-bladeset/blades/helloworld/tests/test-unit/js-test-driver/jsTestDriver.conf");
		String jstestDriverConfContent = FileUtils.readFileToString(jstestDriverConf);

		String oldSrcTestDeclaration = "tests/test-unit/js-test-driver/src-test/emptycorp/a/helloworld/";
		String newSrcTestDeclaration = "tests/test-unit/js-test-driver/src-test/caplinx/a/helloworld/";
		
		assertFalse(jstestDriverConfContent.contains(newSrcTestDeclaration));
		assertTrue(jstestDriverConfContent.contains(oldSrcTestDeclaration));
		
		File testJsFileThatWillBeRenamespaced = new File(applicationDir, "a-bladeset/blades/helloworld/tests/test-unit/js-test-driver/src-test/emptycorp/a/helloworld/HelloWorld.js");
		assertTrue(testJsFileThatWillBeRenamespaced.exists());
		
		Renamer.renameBlade(new File(applicationDir, "a-bladeset/blades/helloworld"), "emptycorp.a.helloworld", "caplinx.a.helloworld");
		
		assertFalse(testJsFileThatWillBeRenamespaced.exists());
		
		File testJsFileThatHasBeenRenamespaced = new File(applicationDir, "a-bladeset/blades/helloworld/tests/test-unit/js-test-driver/src-test/caplinx/a/helloworld/HelloWorld.js");
		assertTrue(testJsFileThatHasBeenRenamespaced.exists());
		
		jstestDriverConfContent = FileUtils.readFileToString(jstestDriverConf);
		assertTrue(jstestDriverConfContent.contains(newSrcTestDeclaration));
		assertFalse(jstestDriverConfContent.contains(oldSrcTestDeclaration));
				
	}
	
	private File createTempAppsDir(File existingApp) throws IOException
	{
		File tempDir = FileUtility.createTemporaryDirectory(this.getClass().getSimpleName());
		
		File rootFolder = existingApp.getParentFile();
		assertTrue(tempDir.exists() && tempDir.isDirectory());
		
		FileUtils.copyDirectory(rootFolder, tempDir);
		
		return new File(tempDir, CutlassConfig.APPLICATIONS_DIR);
	}
	
	private static void assertFileExists(File file)
	{
		assertTrue("Expected " + file + " to exist", file.exists());
	}
	
	private static void assertFileDoesNotExist(File file)
	{
		assertFalse("Expected " + file + " to not exist", file.exists());
	}
}
