package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.DirectoryDoesNotExistCommandException;
import org.bladerunnerjs.plugin.plugins.commands.standard.BundleDepsCommand;
import org.bladerunnerjs.plugin.plugins.commands.standard.InvalidBundlableNodeException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class BundleDepsCommandTest extends SpecTest {
	App app;
	Aspect aspect;
	AliasesFile aliasesFile;
	AliasDefinitionsFile bladeAliasDefinitionsFile;
	Blade blade;
	TestPack bladeTestPack;
	AssetLocation bladeTests;
	private Aspect defaultAspect;
	private Blade bladeInDefaultBladeset;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommandPlugins(new BundleDepsCommand())
			.and(brjs).automaticallyFindsAssetLocationPlugins()
			.and(brjs).automaticallyFindsAssetPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			aspect = app.aspect("default");
			defaultAspect = app.defaultAspect();
			aliasesFile = aspect.aliasesFile();
			blade = app.bladeset("bs").blade("b1");
			bladeAliasDefinitionsFile = blade.assetLocation("src").aliasDefinitionsFile();
			bladeTestPack = blade.testType("unit").testTech("js-test-driver");
			bladeTests = bladeTestPack.tests();
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
	public void bladeTestpendenciesCanBeShown() throws Exception {
		given(bladeTests).containsFileWithContents("MyTest.js", "require('appns/bs/b1/Class1')")
			.and(blade).hasClasses("appns/bs/b1/Class1", "appns/bs/b1/Class2")
			.and(blade).classRequires("appns/bs/b1/Class1", "./Class2");
		when(brjs).runCommand("bundle-deps", "../apps/app/bs-bladeset/blades/b1/test-unit/js-test-driver");
		then(logging).containsConsoleText(
			"Bundle 'apps/app/bs-bladeset/blades/b1/test-unit/js-test-driver' dependencies found:",
			"    +--- 'bs-bladeset/blades/b1/test-unit/js-test-driver/tests/MyTest.js' (seed file)",
			"    |    \\--- 'bs-bladeset/blades/b1/src/appns/bs/b1/Class1.js'",
			"    |    |    \\--- 'bs-bladeset/blades/b1/src/appns/bs/b1/Class2.js'");
	}
	
	@Test
	public void optionalPackageStructuresAreShownCorrectly() throws Exception {
		given(aspect).indexPageRequires("appns/bs/b1/Class1")
    		.and(app.bladeset("bs").blade("b1")).hasClasses("Class1");
    	when(brjs).runCommand("bundle-deps", "../apps/app/default-aspect/");
    	then(logging).containsConsoleText(
    		"Bundle 'apps/app/default-aspect' dependencies found:",
    		"    +--- 'default-aspect/index.html' (seed file)",
    		"    |    \\--- 'bs-bladeset/blades/b1/src/Class1.js'");
	}
	
	@Test
	public void defaultBladesetsAreShownCorrectly() throws Exception {
		given(aspect).indexPageRequires("appns/b1/Class1")
			.and(bladeInDefaultBladeset).hasClasses("appns/b1/Class1");
		when(brjs).runCommand("bundle-deps", "../apps/app/default-aspect/");
		then(logging).containsConsoleText(
			"Bundle 'apps/app/default-aspect' dependencies found:",
			"    +--- 'default-aspect/index.html' (seed file)",
			"    |    \\--- 'blades/b1/src/appns/b1/Class1.js'");
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
