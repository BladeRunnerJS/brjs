package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.model.exception.name.InvalidDirectoryNameException;
import org.bladerunnerjs.model.exception.name.InvalidRootPackageNameException;
import org.bladerunnerjs.plugin.plugins.commands.standard.ExportApplicationCommand;
import org.bladerunnerjs.plugin.plugins.commands.standard.ImportAppCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class ImportAppCommandTest extends SpecTest {
	App app;
	Aspect aspect;
	App importedApp;
	Aspect importedAspect;
	private Bladeset bladeset;
	private Blade blade;
	DirNode appJars;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommandPlugins(new ImportAppCommand(), new ExportApplicationCommand())
			.and(brjs).automaticallyFindsAssetLocationPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			aspect = app.aspect("default");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			importedApp = brjs.app("imported-app");
			importedAspect = importedApp.aspect("default");
			appJars = brjs.appJars();
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
	public void directoriesAreNotDuplicatedWhenExportedAppsAreImportedWithNewNamespace() throws Exception {
		given(aspect).containsFile("src/appns/AspectClass.js")
			.and(bladeset).containsFile("src/appns/bs/BladesetClass.js")
			.and(blade).containsFile("src/appns/bs/b1/BladeClass.js")
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
}
