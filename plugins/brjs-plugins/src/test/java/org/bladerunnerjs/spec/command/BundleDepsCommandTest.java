package org.bladerunnerjs.spec.command;

import static org.bladerunnerjs.plugin.bundlers.aliasing.AliasingUtility.aliasDefinitionsFile;
import static org.bladerunnerjs.plugin.bundlers.aliasing.AliasingUtility.aliasesFile;
import static org.junit.Assert.*;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.model.exception.InvalidBundlableNodeException;
import org.bladerunnerjs.api.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.DirectoryDoesNotExistCommandException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasDefinitionsFile;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasesFile;
import org.bladerunnerjs.plugin.commands.standard.BundleDepsCommand;
import org.bladerunnerjs.spec.aliasing.AliasDefinitionsFileBuilder;
import org.bladerunnerjs.spec.aliasing.AliasesFileBuilder;
import org.junit.Before;
import org.junit.Test;


public class BundleDepsCommandTest extends SpecTest {
	App app;
	Aspect aspect;
	Blade blade;
	TestPack bladeTestPack;
//	AssetLocation bladeTests;
	private Aspect defaultAspect;
	private Blade bladeInDefaultBladeset;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommandPlugins(new BundleDepsCommand())
			.and(brjs).automaticallyFindsAssetPlugins()
			.and(brjs).automaticallyFindsRequirePlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			aspect = app.aspect("default");
			defaultAspect = app.defaultAspect();
			blade = app.bladeset("bs").blade("b1");
			bladeTestPack = blade.testType("unit").testTech("js-test-driver");
//			bladeTests = bladeTestPack.tests();
			bladeInDefaultBladeset = app.defaultBladeset().blade("b1");
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooFewArguments() throws Exception {
		when(brjs).runCommand("bundle-deps");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'bundle-dir' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception {
		when(brjs).runCommand("bundle-deps", "a", "b");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: b"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheDirectoryDoesntExist() throws Exception {
		when(brjs).runCommand("bundle-deps", "../apps/app/default-aspect");
		then(exceptions).verifyException(DirectoryDoesNotExistCommandException.class, unquoted("/apps/app/default-aspect'"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfABundlableNodeCantBeLocated() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("bundle-deps", "../apps");
		then(exceptions).verifyException(InvalidBundlableNodeException.class, "apps")
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(aspect).hasBeenCreated()
			.and(brjs).hasBeenAuthenticallyCreated();
		when(brjs).runCommand("bundle-deps", "../apps/app/default-aspect");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void dependenciesAreShownWhenAllArgumentsAreValid() throws Exception {
		given(aspect).indexPageRequires("appns/Class1")
			.and(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(aspect).classRequires("appns/Class1", "./Class2");
		when(brjs).runCommand("bundle-deps", "../apps/app/default-aspect");
		then(logging).containsConsoleText(
			"Bundle 'apps/app/default-aspect' dependencies found:",
			"    +--- 'default-aspect/index.html' (seed file)",
			"    |    \\--- 'default-aspect/src/appns/Class1.js'",
			"    |    |    \\--- 'default-aspect/src/appns/Class2.js'");
	}
	
	@Test
	public void bladeTestDependenciesCanBeShown() throws Exception {
		given(blade.testType("unit").file("js-test-driver/tests")).containsFileWithContents("MyTest.js", "require('appns/bs/b1/Class1')")
			.and(blade).hasClasses("appns/bs/b1/Class1", "appns/bs/b1/Class2")
			.and(blade).classRequires("appns/bs/b1/Class1", "./Class2");
		when(brjs).runCommand("bundle-deps", "../apps/app/bs-bladeset/blades/b1/test-unit/js-test-driver");
		then(logging).containsConsoleText(
				"Bundle 'apps/app/bs-bladeset/blades/b1/test-unit/js-test-driver' dependencies found:",
				"    +--- 'bs-bladeset/blades/b1/src/appns/bs/b1/Class1.js'",
				"    |    \\--- 'bs-bladeset/blades/b1/src/appns/bs/b1/Class2.js' (static dep.)",
				"    +--- 'bs-bladeset/blades/b1/test-unit/js-test-driver/tests/MyTest.js' (seed file)");

	}
	
	@Test
	public void optionalPackageStructuresAreShownCorrectly() throws Exception {
		given(aspect).indexPageRequires("appns/bs/b1/Class1")
    		.and(app.bladeset("bs").blade("b1")).hasClasses("Class1");
    	when(brjs).runCommand("bundle-deps", "../apps/app/default-aspect/");
    	then(logging).containsConsoleText(
    		"Bundle 'apps/app/default-aspect' dependencies found:",
			"    +--- 'bs-bladeset/blades/b1/src/Class1.js'",
    		"    +--- 'default-aspect/index.html' (seed file)");
	}
	
	@Test
	public void defaultBladesetsAreShownCorrectly() throws Exception {
		given(aspect).indexPageRequires("appns/b1/Class1")
			.and(bladeInDefaultBladeset).hasClasses("appns/b1/Class1");
		when(brjs).runCommand("bundle-deps", "../apps/app/default-aspect/");
		then(logging).containsConsoleText(
			"Bundle 'apps/app/default-aspect' dependencies found:",
			"    +--- 'blades/b1/src/appns/b1/Class1.js'",
			"    +--- 'default-aspect/index.html' (seed file)");
	}
	
	@Test
	public void defaultAspectsAreShownCorrectly() throws Exception {
		given(defaultAspect).indexPageRequires("appns/Class1")
			.and(defaultAspect).hasClasses("appns/Class1");
		when(brjs).runCommand("bundle-deps", "../apps/app");
		then(logging).containsConsoleText(
			"Bundle 'apps/app' dependencies found:",
			"    +--- 'index.html' (seed file)",
			"    |    \\--- 'src/appns/Class1.js'");
	}
	
}
