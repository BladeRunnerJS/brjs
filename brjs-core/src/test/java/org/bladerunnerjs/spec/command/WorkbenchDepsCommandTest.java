package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.plugin.plugins.commands.standard.WorkbenchDepsCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class WorkbenchDepsCommandTest extends SpecTest {
	App app;
	Aspect aspect;
	Bladeset bladeset;
	Blade blade;
	Workbench workbench;
	JsLib brLib;
	AliasDefinitionsFile brLibAliasDefinitionsFile;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommands(new WorkbenchDepsCommand())
			.and(brjs).automaticallyFindsAssetLocationProducers()
			.and(brjs).automaticallyFindsAssetProducers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			aspect = app.aspect("default");
			bladeset = app.bladeset("bladeset");
			blade = bladeset.blade("blade");
			workbench = blade.workbench();
			brLib = brjs.sdkLib("br");
			brLibAliasDefinitionsFile = brLib.assetLocation("resources").aliasDefinitionsFile();
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
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted(app.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheBladesetDoesntExist() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("workbench-deps", "app", "bladeset", "blade");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted(bladeset.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheBladeDoesntExist() throws Exception {
		given(bladeset).hasBeenCreated();
		when(brjs).runCommand("workbench-deps", "app", "bladeset", "blade");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted(blade.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheWorkbenchDoesntExist() throws Exception {
		given(blade).hasBeenCreated();
		when(brjs).runCommand("workbench-deps", "app", "bladeset", "blade");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted(workbench.getClass().getSimpleName()))
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
		given(workbench).indexPageRequires("appns/Class1")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).classRequires("appns.Class1", "./Class2");
		when(brjs).runCommand("workbench-deps", "app", "bladeset", "blade");
		then(output).containsText(
			"Workbench dependencies found:",
			"    +--- 'bladeset-bladeset/blades/blade/workbench/index.html' (seed file)",
			"    |    \\--- 'default-aspect/src/appns/Class1.js'",
			"    |    |    \\--- 'default-aspect/src/appns/Class2.js'");
	}
	
	// TODO understand why this test thorws an UnresolvedAliasException
	// Update the output string as necessary, verifying that the br.Class2 dependency gets correctly displayed
	@Ignore
	@Test
	public void dependenciesAreShownForWorkbenchUsingAliasWhenAllArgumentsAreValid() throws Exception {
		given(brLib).hasClasses("br.Class1", "br.Class2")
			.and(brLibAliasDefinitionsFile).hasAlias("br.alias", "br.Class2")
			.and(blade).classFileHasContent("appns/bladeset/blade/Class1", "ServiceRegistry.getService('br.alias')")
			.and(workbench).indexPageRequires("appns/bladeset/blade/Class1");
		when(brjs).runCommand("workbench-deps", "app", "bladeset", "blade");
		then(output).containsText(
			"Workbench dependencies found:",
			"    +--- 'bladeset-bladeset/blades/blade/workbench/index.html' (seed file)",
			"    |    \\--- 'default-aspect/src/appns/Class1.js'",
			"    |    |    \\--- 'default-aspect/src/appns/Class2.js'");
	}
}
