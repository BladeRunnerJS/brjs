package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.plugin.plugins.commands.standard.DepInsightCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class DepInsightCommandTest extends SpecTest {
	App app;
	Aspect aspect;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommands(new DepInsightCommand())
			.and(brjs).automaticallyFindsAssetLocationProducers()
			.and(brjs).automaticallyFindsAssetProducers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			aspect = app.aspect("default");
	}
	
	@Test
	public void exceptionIsThrownIfTheAppNameIsNotProvided() throws Exception {
		when(brjs).runCommand("dep-insight");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'app-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheRequirePathIsNotProvided() throws Exception {
		when(brjs).runCommand("dep-insight", "app");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'require-path' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception {
		when(brjs).runCommand("dep-insight", "a", "b", "c", "d");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: d"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAppDoesntExist() throws Exception {
		when(brjs).runCommand("dep-insight", "app", "require-path");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted(app.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheDefaultAspectDoesntExist() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("dep-insight", "app", "require-path");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted(aspect.getClass().getSimpleName()), "default")
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheNamedAspectDoesntExist() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("dep-insight", "app", "require-path", "aspect");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted(aspect.getClass().getSimpleName()), "aspect")
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(aspect).hasClass("appns.Class");
		when(brjs).runCommand("dep-insight", "app", "appns/Class");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void dependenciesAreShownWhenAllArgumentsAreValid() throws Exception {
		given(aspect).indexPageRequires("appns/Class1")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).classRequires("appns.Class1", "./Class2");
		when(brjs).runCommand("dep-insight", "app", "appns/Class2");
		then(output).containsText(
			"Source module 'appns/Class2' dependencies found:",
			"    +--- 'default-aspect/src/appns/Class2.js'",
			"    |    \\--- 'default-aspect/src/appns/Class1.js'",
			"    |    |    \\--- 'default-aspect/index.html'");
	}
	
	@Test
	public void resourceDependenciesAreShownAheadOfClassDependenciesSinceTheyReflectUltimateLeafNodesOfGreaterImportanceToTheUser() throws Exception {
		given(aspect).indexPageRequires("appns/Class1")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).classRequires("appns.Class1", "./Class2")
			.and(aspect).containsFileWithContents("resources/config.xml", "'appns/Class2'");
		when(brjs).runCommand("dep-insight", "app", "appns/Class2");
		then(output).containsText(
			"Source module 'appns/Class2' dependencies found:",
			"    +--- 'default-aspect/src/appns/Class2.js'",
			"    |    \\--- 'default-aspect/resources/config.xml' (seed file)",
			"    |    \\--- 'default-aspect/src/appns/Class1.js'",
			"    |    |    \\--- 'default-aspect/index.html' (seed file)");
	}
	
	@Test
	public void ifTheSameAssetIsFoundTwiceThenItsDependenciesAreOnlyShownTheFirstTime() throws Exception {
		given(aspect).indexPageRequires("appns/Class1")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).classRequires("appns.Class1", "./Class2")
			.and(aspect).classRequires("appns.Class2", "./Class3")
			.and(aspect).classRequires("appns.Class3", "./Class1");
		when(brjs).runCommand("dep-insight", "app", "appns/Class3");
		then(output).containsText(
			"Source module 'appns/Class3' dependencies found:",
			"    +--- 'default-aspect/src/appns/Class3.js'",
			"    |    \\--- 'default-aspect/src/appns/Class2.js'",
			"    |    |    \\--- 'default-aspect/src/appns/Class1.js'",
			"    |    |    |    \\--- 'default-aspect/index.html' (seed file)",
			"    |    |    |    \\--- 'default-aspect/src/appns/Class3.js' (*)",
			"",
			"    (*) - dependencies omitted (listed previously)");
	}
	
	@Test
	public void dependenciesThatOccurDueToRelatedResourcesAreShownCorrectly() throws Exception {
		given(aspect).indexPageRequires("appns/Class1")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2", "appns.pkg.InnerClass")
			.and(aspect).classRequires("appns.Class1", "./pkg/InnerClass")
			.and(aspect).containsFileWithContents("src/appns/pkg/config.xml", "'appns/Class2'")
			.and(aspect).containsEmptyFile("empty-config.xml");
		when(brjs).runCommand("dep-insight", "app", "appns/Class2");
		then(output).containsText(
			"Source module 'appns/Class2' dependencies found:",
			"    +--- 'default-aspect/src/appns/Class2.js'",
			"    |    \\--- 'default-aspect/src/appns/pkg/config.xml' (implicit resource)",
			"    |    |    \\--- 'default-aspect/src/appns/pkg/InnerClass.js'",
			"    |    |    |    \\--- 'default-aspect/src/appns/Class1.js'",
			"    |    |    |    |    \\--- 'default-aspect/index.html' (seed file)");
	}
}
