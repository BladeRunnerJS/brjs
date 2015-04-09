package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.api.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.BladeWorkbench;
import org.bladerunnerjs.plugin.commands.standard.WorkbenchDepsCommand;
import org.junit.Before;
import org.junit.Test;


public class WorkbenchDepsCommandTest extends SpecTest {
	App app;
	Aspect aspect;
	Bladeset bladeset;
	Blade blade;
	BladeWorkbench workbench;
	JsLib brLib;
	AliasDefinitionsFile brLibAliasDefinitionsFile;
	private Blade bladeInDefaultBladeset;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommandPlugins(new WorkbenchDepsCommand())
			.and(brjs).automaticallyFindsAssetLocationPlugins()
			.and(brjs).automaticallyFindsAssetPlugins()
			.and(brjs).automaticallyFindsRequirePlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			aspect = app.aspect("default");
			bladeset = app.bladeset("bladeset");
			blade = bladeset.blade("blade");
			workbench = blade.workbench();
			brLib = brjs.sdkLib("br");
			brLibAliasDefinitionsFile = brLib.assetLocation("resources").aliasDefinitionsFile();
			bladeInDefaultBladeset = app.defaultBladeset().blade("blade");
	}
	
	@Test
	public void resourceFilesInTheAspectThatReferenceAspectSrcDontCauseAnException() throws Exception {
		given(aspect).hasClasses("appns/Class1")
	 		.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1")
	 		.and(workbench).indexPageHasContent("");
	 	when(brjs).runCommand("workbench-deps", "app", "bladeset", "blade");
	 	then(logging).doesNotcontainConsoleText("default-aspect");
	}
	
	@Test
	public void exceptionIsThrownIfNoAppNameIsProvided() throws Exception {
		when(brjs).runCommand("workbench-deps");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'app-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfNoBladesetNameIsProvided() throws Exception {
		when(brjs).runCommand("workbench-deps", "app");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'bladeset-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfNoBladeNameIsProvided() throws Exception {
		when(brjs).runCommand("workbench-deps", "app", "bladeset");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'blade-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception {
		when(brjs).runCommand("workbench-deps", "a", "b", "c", "d");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: d"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAppDoesntExist() throws Exception {
		when(brjs).runCommand("workbench-deps", "app", "bladeset", "blade");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted(app.getTypeName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheBladesetDoesntExist() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("workbench-deps", "app", "bladeset", "blade");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted(bladeset.getTypeName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheBladeDoesntExist() throws Exception {
		given(bladeset).hasBeenCreated();
		when(brjs).runCommand("workbench-deps", "app", "bladeset", "blade");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted(blade.getTypeName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheWorkbenchDoesntExist() throws Exception {
		given(blade).hasBeenCreated();
		when(brjs).runCommand("workbench-deps", "app", "bladeset", "blade");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted(workbench.getTypeName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(workbench).hasBeenCreated();
		when(brjs).runCommand("workbench-deps", "app", "bladeset", "blade");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void dependenciesAreShownWhenAllArgumentsAreValid() throws Exception {
		given(workbench).indexPageRequires("appns/bladeset/blade/Class1")
			.and(blade).hasClasses("appns/bladeset/blade/Class1", "appns/bladeset/blade/Class2")
			.and(blade).classRequires("appns/bladeset/blade/Class1", "./Class2");
		when(brjs).runCommand("workbench-deps", "app", "bladeset", "blade");
		then(logging).containsConsoleText(
			"Workbench dependencies found:",
			"    +--- 'bladeset-bladeset/blades/blade/workbench/index.html' (seed file)",
			"    |    \\--- 'bladeset-bladeset/blades/blade/src/appns/bladeset/blade/Class1.js'",
			"    |    |    \\--- 'bladeset-bladeset/blades/blade/src/appns/bladeset/blade/Class2.js'");
	}
	
	@Test
	public void ifTheSameAssetIsFoundTwiceThenOnlyTheFirstEncounteredInstanceIsShownByDefault() throws Exception {
		given(workbench).indexPageRequires("appns/bladeset/blade/Class1")
			.and(blade).hasClasses("appns/bladeset/blade/Class1", "appns/bladeset/blade/Class2")
			.and(blade).classRequires("appns/bladeset/blade/Class1", "./Class2")
			.and(workbench).containsResourceFileWithContents("config.xml", "'appns/bladeset/blade/Class1'"); // TODO: if we make this an aspect resource it doesn't work suggesting a bug -- make me feel even more strongly there should be a method which provides all asset locations, including transitive deps.
		when(brjs).runCommand("workbench-deps", "app", "bladeset", "blade");
		then(logging).containsConsoleText(
			"Workbench dependencies found:",
			"    +--- 'bladeset-bladeset/blades/blade/workbench/index.html' (seed file)",
			"    |    \\--- 'bladeset-bladeset/blades/blade/src/appns/bladeset/blade/Class1.js' (*)",
			"    |    |    \\--- 'bladeset-bladeset/blades/blade/src/appns/bladeset/blade/Class2.js' (static dep.)",
			"    +--- 'bladeset-bladeset/blades/blade/workbench/resources/config.xml' (seed file)",
			"",
			"    (*) - subsequent instances not shown (use -A or --all to show)");
	}
	
	@Test
	public void withTheAllSwitchIfTheSameAssetIsFoundTwiceThenItsDependenciesAreOnlyShownTheFirstTime() throws Exception {
		given(workbench).indexPageRequires("appns/bladeset/blade/Class1")
			.and(blade).hasClasses("appns/bladeset/blade/Class1", "appns/bladeset/blade/Class2")
			.and(blade).classRequires("appns/bladeset/blade/Class1", "./Class2")
			.and(workbench).containsResourceFileWithContents("config.xml", "'appns/bladeset/blade/Class1'");
		when(brjs).runCommand("workbench-deps", "app", "bladeset", "blade", "--all");
		then(logging).containsConsoleText(
			"Workbench dependencies found:",
			"    +--- 'bladeset-bladeset/blades/blade/workbench/index.html' (seed file)",
			"    |    \\--- 'bladeset-bladeset/blades/blade/src/appns/bladeset/blade/Class1.js'",
			"    |    |    \\--- 'bladeset-bladeset/blades/blade/src/appns/bladeset/blade/Class2.js' (static dep.)",
			"    +--- 'bladeset-bladeset/blades/blade/workbench/resources/config.xml' (seed file)",
			"    |    \\--- 'bladeset-bladeset/blades/blade/src/appns/bladeset/blade/Class1.js' (*)",
			"",
			"    (*) - dependencies omitted (listed previously)");
	}
	
	@Test
	public void dependenciesAreShownForWorkbenchUsingAliasWhenAllArgumentsAreValid() throws Exception {
		given(brLib).hasClasses("br/Class1", "br/Class2", "br/AliasInterfaceError")
			.and(brLibAliasDefinitionsFile).hasAlias("br.alias", "br.Class2")
			.and(blade).classFileHasContent("appns/bladeset/blade/Class1", "require('alias!br.alias')")
			.and(workbench).indexPageRequires("appns/bladeset/blade/Class1");
		when(brjs).runCommand("workbench-deps", "app", "bladeset", "blade");
		then(logging).containsConsoleText(
				"Workbench dependencies found:",
				"    +--- 'bladeset-bladeset/blades/blade/workbench/index.html' (seed file)",
				"    |    \\--- 'bladeset-bladeset/blades/blade/src/appns/bladeset/blade/Class1.js'",
				"    |    |    \\--- 'alias!br.alias' (static dep.)",
				"    |    |    |    \\--- '../../libs/javascript/br/src/br/Class2.js' (static dep.)");
	}
	
	@Test
	public void optionalPackageStructuresAreShownCorrectly() throws Exception {
		given(workbench).indexPageRequires("appns/bladeset/blade/Class1")
    		.and(blade).hasClasses("Class1");
		when(brjs).runCommand("workbench-deps", "app", "bladeset", "blade");
    	then(logging).containsConsoleText(
    		"Workbench dependencies found:",
    		"    +--- 'bladeset-bladeset/blades/blade/workbench/index.html' (seed file)",
			"    |    \\--- 'bladeset-bladeset/blades/blade/src/Class1.js'");
	}
	
	@Test
	public void defaultBladesetsAreShownCorrectly() throws Exception {
		given( bladeInDefaultBladeset.workbench() ).indexPageRequires("appns/blade/Class1")
    		.and( bladeInDefaultBladeset ).hasClasses("Class1");
    	when(brjs).runCommand("workbench-deps", "app", "default", "blade");
    	then(logging).containsConsoleText(
    		"Workbench dependencies found:",
    		"    +--- 'blades/blade/workbench/index.html' (seed file)",
    		"    |    \\--- 'blades/blade/src/Class1.js'");
	}
	
}
