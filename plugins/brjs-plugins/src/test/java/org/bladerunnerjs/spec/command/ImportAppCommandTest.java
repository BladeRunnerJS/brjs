package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.BladesetWorkbench;
import org.bladerunnerjs.api.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.api.model.exception.name.InvalidDirectoryNameException;
import org.bladerunnerjs.api.model.exception.name.InvalidRootPackageNameException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.api.DirNode;
import org.bladerunnerjs.api.BladeWorkbench;
import org.bladerunnerjs.plugin.commands.standard.ExportApplicationCommand;
import org.bladerunnerjs.plugin.commands.standard.ImportAppCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


@SuppressWarnings("unused")
public class ImportAppCommandTest extends SpecTest {
	App app;
	Aspect aspect;
	App importedApp;
	Aspect importedAspect;
	private Bladeset bladeset;
	private Blade blade1, blade2;
	private BladeWorkbench workbench;
	DirNode appJars;
	private BladesetWorkbench bladesetWorkbench;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommandPlugins(new ImportAppCommand(), new ExportApplicationCommand())
			.and(brjs).automaticallyFindsAssetPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			aspect = app.aspect("default");
			bladeset = app.bladeset("bs");
			bladesetWorkbench = bladeset.workbench();
			blade1 = bladeset.blade("b1");
			blade2 = bladeset.blade("b2");
			workbench = blade1.workbench();
			importedApp = brjs.app("imported-app");
			importedAspect = importedApp.aspect("default");
			appJars = brjs.appJars();
	}
	
	@After
	public void tearDown() {
		try
		{
			brjs.applicationServer(appServerPort).stop();
		}
		catch (Exception e)
		{
			// ignore the exception
		}
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooFewArguments() throws Exception {
		when(brjs).runCommand("import-app", "a", "b");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'new-app-require-prefix' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception {
		when(brjs).runCommand("import-app", "a", "b", "c", "d");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: d"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAppZipDoesntExist() throws Exception {
		when(brjs).runCommand("import-app", "non-existent-app.zip", "b", "c");
		then(exceptions).verifyException(CommandArgumentsException.class, "non-existent-app.zip");
	}
	
	@Test
	public void exceptionIsThrownIfTheAppAlreadyExists() throws Exception {
		given(brjs).containsFile("sdk/app.zip")
			.and(app).hasBeenCreated();
		when(brjs).runCommand("import-app", "app.zip", "app", "appns");
		then(exceptions).verifyException(NodeAlreadyExistsException.class, "app")
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAppNameIsInvalid() throws Exception {
		given(brjs).containsFile("sdk/app.zip");
		when(brjs).runCommand("import-app", "app.zip", "app 1", "appns");
		then(exceptions).verifyException(InvalidDirectoryNameException.class, "app 1")
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheRequirePrefixIsInvalid() throws Exception {
		given(brjs).containsFile("sdk/app.zip");
		when(brjs).runCommand("import-app", "app.zip", "app", "$appns");
		then(exceptions).verifyException(InvalidRootPackageNameException.class, "$appns")
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exportedAppsCanBeReimported() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).classRequires("appns/Class2", "appns/Class1")
			.and(brjs).commandHasBeenRun("export-app", "app")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedAspect).fileContentsContains("src/importedns/Class2.js", "require('importedns/Class1')")
			.and(importedApp).hasFile("WEB-INF/lib/brjs-lib1.jar");
	}
	
	@Test
	public void exportedAppsCanBeReimportedWithADifferentModel() throws Exception {
		given(aspect).hasClass("appns/Class1")
    		.and(aspect).classRequires("appns/Class2", "appns/Class1")
    		.and(brjs).commandHasBeenRun("export-app", "app")
    		.and(appJars).containsFile("brjs-lib1.jar")
    		.and(brjs).hasBeenAuthenticallyReCreated();
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedAspect).fileContentsContains("src/importedns/Class2.js", "require('importedns/Class1')")
			.and(importedApp).hasFile("WEB-INF/lib/brjs-lib1.jar");
	}
	
	@Test
	public void directoriesAreNotDuplicatedWhenExportedAppsAreImportedWithNewNamespace() throws Exception {
		given(aspect).containsFile("src/appns/AspectClass.js")
			.and(bladeset).containsFile("src/appns/bs/BladesetClass.js")
			.and(blade1).containsFile("src/appns/bs/b1/BladeClass.js")
			.and(brjs).commandHasBeenRun("export-app", "app")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedApp).hasDir("bs-bladeset/blades/b1/src/importedns")
			.and(importedApp).hasDir("bs-bladeset/src/importedns")
			.and(importedApp).hasDir("default-aspect/src/importedns")
			.and(importedApp).doesNotHaveDir("bs-bladeset/src/appns")
			.and(importedApp).doesNotHaveDir("bs-bladeset/blades/b1/src/appns")
			.and(importedApp).doesNotHaveDir("default-aspect/src/appns");
	}
	
	@Test
	public void FilesAtAspectRootLevelAreRenameSpacedCorrectlyAfterImportApp() throws Exception {
		given(aspect).containsFileWithContents("index.html", "require( 'appns/App' );")
			.and(brjs).commandHasBeenRun("export-app", "app")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedApp).fileHasContents("default-aspect/index.html", "require( 'importedns/App' );");
	}
	
	@Test
	public void FilesAtWorkbenchRootLevelAreRenameSpacedCorrectlyAfterImportApp() throws Exception {
		given(workbench).containsFileWithContents("index.html", "require( 'appns/App' );")
			.and(brjs).commandHasBeenRun("export-app", "app")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedApp).fileHasContents("bs-bladeset/blades/b1/workbench/index.html", "require( 'importedns/App' );");
	}
	
	@Test
	public void importingAnAppDoesntChangeTheAppItWasExportedFrom() throws Exception {
		given(aspect).containsFileWithContents("src/appns/AspectClass.js", "some aspect class contents")
			.and(brjs).commandHasBeenRun("export-app", "app")
			.and(aspect).containsFileWithContents("src/appns/AspectClass.js", "some NEW aspect class contents")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedApp).fileHasContents("default-aspect/src/importedns/AspectClass.js", "some aspect class contents");
	}
	
	@Test
	public void defaultAspectsAreCorrectlyImported() throws Exception {
		given(app).hasBeenCreated()
			.and(app.defaultAspect()).indexPageHasContent("default aspect index")
			.and(brjs).commandHasBeenRun("export-app", "app")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedApp).fileContentsContains("index.html", "default aspect index");
	}
	
	@Test
	public void defaultBladesetsAreCorrectlyImported() throws Exception {
		given(app).hasBeenCreated()
			.and(app.defaultBladeset().blade("b1")).classFileHasContent("Class1", "default-bladeset/b1/Class")
			.and(brjs).commandHasBeenRun("export-app", "app")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedApp).fileContentsContains("blades/b1/src/Class1.js", "default-bladeset/b1/Class");
	}
	
	@Test
	public void allSrcDirectoriesAreCorrectlyReNamespacedWhenImported() throws Exception {
		given(aspect).containsFile("src/appns/AspectClass.js")
			.and(bladeset).containsFile("src/appns/bs/BladesetClass.js")
			.and(blade1).containsFile("src/appns/bs/b1/BladeClass.js")
			.and(workbench).containsFile("src/appns/bs/b1/WorkbenchClass.js")
			.and(brjs).commandHasBeenRun("export-app", "app")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedApp).hasDir("bs-bladeset/blades/b1/src/importedns")
			.and(importedApp).hasDir("bs-bladeset/src/importedns")
			.and(importedApp).hasDir("default-aspect/src/importedns")
			.and(importedApp).hasDir("bs-bladeset/blades/b1/workbench/src/importedns/bs/b1/");
	}
	
	@Test
	public void allTestDirectoriesAreCorrectlyReNamespacedWhenImported() throws Exception {
		given(aspect).containsFile("tests/appns/AspectTestClass.js")
			.and(aspect).containsFile("tests/test-unit/js-test-driver/src-test/appns/AspectTestClass.js")
			.and(aspect).containsFile("tests/test-unit/js-test-driver/tests/appns/AspectTest.js")
			.and(bladeset).containsFile("tests/test-unit/js-test-driver/src-test/appns/bs/BladeTestClass.js")
			.and(bladeset).containsFile("tests/test-unit/js-test-driver/tests/appns/bs/BladeTest.js")
			.and(blade1).containsFile("src/appns/bs/b1/BladeClass.js")
			.and(blade1).containsFile("tests/test-unit/js-test-driver/src-test/appns/bs/b1/BladeTestClass.js")
			.and(blade1).containsFile("tests/test-unit/js-test-driver/tests/appns/bs/b1/BladeTest.js")
			.and(brjs).commandHasBeenRun("export-app", "app")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedApp).hasDir("bs-bladeset/blades/b1/tests/test-unit/js-test-driver/src-test/importedns/")
			.and(importedApp).hasDir("bs-bladeset/blades/b1/tests/test-unit/js-test-driver/tests/importedns/")
			.and(importedApp).hasDir("bs-bladeset/tests/test-unit/js-test-driver/src-test/importedns/")
			.and(importedApp).hasDir("bs-bladeset/tests/test-unit/js-test-driver/tests/importedns/")
			.and(importedApp).hasDir("default-aspect/tests/test-unit/js-test-driver/src-test/importedns/")
			.and(importedApp).hasDir("default-aspect/tests/test-unit/js-test-driver/tests/importedns/");
	}
	
	@Test
	public void oldAppNamePrefixedAndFollowedByASlashIsReplacedInJettyEnv() throws Exception {
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("WEB-INF/jetty-env.xml", "/app/some-url" )
			.and(brjs).commandHasBeenRun("export-app", "app")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedApp).fileContentsContains("WEB-INF/jetty-env.xml", "/imported-app/some-url");
	}
	
	@Test
	public void jndiConfigUrlIsRenamespaced() throws Exception {
		given( brjs.app("myapp") ).hasBeenCreated()
			.and( brjs.app("myapp") ).containsFileWithContents("WEB-INF/jetty-env.xml", "jdbc:h2:../generated/app/myapp/somedb/myapp;someDB=config" )
			.and(brjs).commandHasBeenRun("export-app", "myapp")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/myapp.zip", "imported-app", "importedns");
		then(importedApp).fileContentsEquals("WEB-INF/jetty-env.xml", "jdbc:h2:../generated/app/imported-app/somedb/imported-app;someDB=config");
	}
	
	@Test
	public void oldAppNamePrefixedByASlashAndFollowedByASemicolonIsReplacedInJettyEnv() throws Exception {
		given(app).hasBeenCreated()
		.and(app).containsFileWithContents("WEB-INF/jetty-env.xml", "/app;" )
			.and(brjs).commandHasBeenRun("export-app", "app")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedApp).fileContentsContains("WEB-INF/jetty-env.xml", "/imported-app;");
	}
	
	@Test
	public void appNameIsReplacedIfPreviousAppRequirePrefixWasTheSameAsTheAppNameAndADefaultAspectIsUsed() throws Exception {
		given(app).hasBeenCreated()
			.and(app.appConf()).hasRequirePrefix(app.getName())
			.and(app.defaultAspect()).containsFile("index.html")
			.and(app).containsFileWithContents("WEB-INF/jetty-env.xml", "/app/some-url" )
			.and(brjs).commandHasBeenRun("export-app", "app")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedApp).fileContentsContains("WEB-INF/jetty-env.xml", "/imported-app/some-url");
	}
	
	@Test
	public void oldAppNameNotFollowedByASlashIsNotReplacedInJettyEnv() throws Exception {
		given(app).hasBeenCreated()
		.and(app).containsFileWithContents("WEB-INF/jetty-env.xml", "here be webapps. and my app" )
			.and(brjs).commandHasBeenRun("export-app", "app")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedApp).fileContentsContains("WEB-INF/jetty-env.xml", "here be webapps. and my app");
	}

	@Test
	public void oldAppNameIsNotReplacedInOtherXmlFiles() throws Exception {
		given(app).hasBeenCreated()
		.and(app).containsFileWithContents("WEB-INF/web.xml", "/app/some-url" )
			.and(brjs).commandHasBeenRun("export-app", "app")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedApp).fileContentsContains("WEB-INF/web.xml", "/app/some-url");
	}

	@Test
	public void oldAppNameIsReplacedInABladesetlessBladeAliasesXml() throws Exception {
		given(app).hasBeenCreated()
			.and(app.defaultBladeset().blade("myblade")).containsFileWithContents("resources/aliases.xml", 
				"<alias name=\"bar.user-prompt-service\" class=\"appns.myblade.MyUserPromptService\"/>")
			.and(brjs).commandHasBeenRun("export-app", "app")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedApp.defaultBladeset().blade("myblade")).fileContentsContains("resources/aliases.xml", 
				"<alias name=\"bar.user-prompt-service\" class=\"importedns.myblade.MyUserPromptService\"/>");
	}
	
	@Test // This test attempts to reproduce a bug we were seeing in the product - https://github.com/BladeRunnerJS/brjs/issues/1238
	public void bladesetWorkbenchCanBeLoadedWithoutClassCastExceptionAfterImportInANewBRJSProcess() throws Exception {
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
					+ "locales: en\n"
					+ "requirePrefix: appns")
			.and(bladeset).hasBeenCreated()
			.and(bladesetWorkbench).containsFileWithContents("resources/css/style.css", "url('./file.png')")
			.and(bladesetWorkbench).containsFileWithContents("resources/file.png", "my cool image")
			.and(bladesetWorkbench).indexPageHasContent("")
			.and(brjs.appJars()).hasBeenCreated()
			.and(brjs).hasBeenAuthenticallyReCreated();
		when(brjs.applicationServer(appServerPort)).started();
		then(brjs.applicationServer(appServerPort)).requestForUrlContains(
				"/app/bs/workbench/v/dev/css/common/bundle.css", "../../cssresource/bladeset_bs/workbench_resource/resources/css/file.png");
	}

	@Test // test to replicate bugs from https://github.com/BladeRunnerJS/brjs/issues/1315
	public void contentIsCorrectlyRenamespacedWhenOldNamespacesArePrecededWithASlash() throws Exception {
		given(app).hasBeenCreated()
			.and(app.defaultBladeset().blade("myblade")).containsFileWithContents("resources/aliases.xml", 
				"<appns.myblade.SomeTag>tag content</appns.myblade.SomeTag>")
			.and(brjs).commandHasBeenRun("export-app", "app")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedApp.defaultBladeset().blade("myblade")).fileContentsContains("resources/aliases.xml", 
				"<importedns.myblade.SomeTag>tag content</importedns.myblade.SomeTag>");
	}
	
	@Test // test to replicate bugs from https://github.com/BladeRunnerJS/brjs/issues/1315
	public void contentIsCorrectlyRenamespacedWhenTheFileContainsAPoundSign() throws Exception {
		given(app).hasBeenCreated()
			.and(app.defaultBladeset().blade("myblade")).containsFileWithContents("resources/aliases.xml", 
				"<appns.myblade.SomeTag>tag £££ content</appns.myblade.SomeTag>")
				.and(app.defaultBladeset().blade("myblade").testType("unit").defaultTestTech()).containsFileWithContents("tests/Test.js", "££££ appns.myblade.SomeClass")
			.and(brjs).commandHasBeenRun("export-app", "app")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedApp.defaultBladeset().blade("myblade")).fileContentsContains("resources/aliases.xml", 
				"<importedns.myblade.SomeTag>tag £££ content</importedns.myblade.SomeTag>")
			.and(importedApp.defaultBladeset().blade("myblade")).fileContentsContains("test-unit/tests/Test.js", 
						"££££ importedns.myblade.SomeClass");
				
	}
	
	@Test
	public void imagesArentCorrupOnImport() throws Exception {
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
    		.and(aspect).containsFileCopiedFrom("resources/br-logo.png", "src/test/resources/br-logo.png")
    		.and(brjs).commandHasBeenRun("export-app", "app")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedAspect.file("resources/br-logo.png")).contentsTheSameAsFile("src/test/resources/br-logo.png");
	}
	
	@Test
	public void referencesToBladeCodeFromUnbundledResourcesIsReplaced() throws Exception {
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsFileWithContents("unbundled-resources/file.txt", "appns.bs.b1.Class")
    		.and(brjs).commandHasBeenRun("export-app", "app")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedAspect).fileContentsEquals("unbundled-resources/file.txt", "importedns.bs.b1.Class");
	}
	
	@Test // new apps shouldnt have Blade -> another Blade deps but we need to support this for backwards compatibility
	public void referencesToAClassFromOneBladeToAnotherIsReplacead() throws Exception {
		given(app).hasBeenCreated()
			.and(bladeset).hasBeenCreated()
			.and(blade1).hasBeenCreated()
			.and(blade2).classFileHasContent("appns/bs/b2/Blade2Class", "appns.bs.b1.Blade1Class")
    		.and(brjs).commandHasBeenRun("export-app", "app")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedApp.bladeset("bs").blade("b2")).fileContentsEquals("src/importedns/bs/b2/Blade2Class.js", "importedns.bs.b1.Blade1Class");
	}
	
	@Test
	public void referencesToAParentBladesetIsReplacead() throws Exception {
		given(app).hasBeenCreated()
			.and(bladeset).hasBeenCreated()
			.and(blade1).classFileHasContent("appns/bs/b1/BladeClass", "appns.bs.BladesetClass")
    		.and(brjs).commandHasBeenRun("export-app", "app")
			.and(appJars).containsFile("brjs-lib1.jar");
		when(brjs).runCommand("import-app", "../generated/exported-apps/app.zip", "imported-app", "importedns");
		then(importedApp.bladeset("bs").blade("b1")).fileContentsEquals("src/importedns/bs/b1/BladeClass.js", "importedns.bs.BladesetClass");
	}
	
}
