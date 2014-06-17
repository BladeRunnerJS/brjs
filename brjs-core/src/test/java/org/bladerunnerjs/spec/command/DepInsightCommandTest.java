package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.plugin.plugins.commands.standard.DepInsightCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class DepInsightCommandTest extends SpecTest {
	App app;
	Aspect aspect;
	AliasesFile aliasesFile;
	AliasDefinitionsFile bladeAliasDefinitionsFile;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommandPlugins(new DepInsightCommand())
			.and(brjs).automaticallyFindsAssetLocationPlugins()
			.and(brjs).automaticallyFindsAssetPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			aspect = app.aspect("default");
			aliasesFile = aspect.aliasesFile();
			bladeAliasDefinitionsFile = app.bladeset("bs").blade("b1").assetLocation("src").aliasDefinitionsFile();
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
	public void exceptionIsThrownIfPrefixAndAliasSwitchesAreUsedSimultaneously() throws Exception {
		given(aspect).hasBeenCreated();
		when(brjs).runCommand("dep-insight", "app", "require-path", "--prefix", "--alias");
		then(exceptions).verifyException(CommandArgumentsException.class, unquoted("The --prefix and --alias switches can't both be used at the same time"));
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(aspect).hasClass("appns/Class");
		when(brjs).runCommand("dep-insight", "app", "appns/Class");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void dependenciesAreShownWhenAllArgumentsAreValid() throws Exception {
		given(aspect).indexPageRequires("appns/Class1")
			.and(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(aspect).classRequires("appns/Class1", "./Class2");
		when(brjs).runCommand("dep-insight", "app", "appns/Class2");
		then(output).containsText(
			"Source module 'appns/Class2' dependencies found:",
			"    +--- 'default-aspect/src/appns/Class2.js'",
			"    |    \\--- 'default-aspect/src/appns/Class1.js'",
			"    |    |    \\--- 'default-aspect/index.html'");
	}
	
	@Test
	public void dependenciesAreShownForNamespacedClassesToo() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).indexPageRequires("appns/Class1")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).classDependsOn("appns.Class1", "appns.Class2");
		when(brjs).runCommand("dep-insight", "app", "appns/Class2");
		then(output).containsText(
			"Source module 'appns/Class2' dependencies found:",
			"    +--- 'default-aspect/src/appns/Class2.js'",
			"    |    \\--- 'default-aspect/src/appns/Class1.js'",
			"    |    |    \\--- 'default-aspect/index.html'");
	}
	
	@Test
	public void onlyDependenciesThatAreToBeBundledAreShown() throws Exception {
		given(aspect).indexPageRequires("appns/Class2")
			.and(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(aspect).classRequires("appns/Class1", "./Class2");
		when(brjs).runCommand("dep-insight", "app", "appns/Class2");
		then(output).containsText(
			"Source module 'appns/Class2' dependencies found:",
			"    +--- 'default-aspect/src/appns/Class2.js'",
			"    |    \\--- 'default-aspect/index.html'");
	}
	
	@Test
	public void ifTheSourceModuleBeingInspectedIsntToBeBundledThenAllDependenciesAreShown() throws Exception {
		given(aspect).indexPageRequires("appns/Class3")
			.and(aspect).hasClasses("appns/Class1", "appns/Class2", "appns/Class3")
			.and(aspect).classRequires("appns/Class1", "./Class2");
		when(brjs).runCommand("dep-insight", "app", "appns/Class2");
		then(output).containsText(
			"Source module 'appns/Class2' dependencies found:",
			"    +--- 'default-aspect/src/appns/Class2.js'",
			"    |    \\--- 'default-aspect/src/appns/Class1.js'");
	}
	
	@Test
	public void requestingDependenciesForANonExistentSourceModuleProvidesANiceMessage() throws Exception {
		given(aspect).hasBeenCreated();
		when(brjs).runCommand("dep-insight", "app", "NonExistentClass");
		then(output).containsText(
			"Source file 'NonExistentClass' could not be found.");
	}
	
	@Test
	public void resourceDependenciesAreShownAheadOfClassDependenciesSinceTheyReflectUltimateLeafNodesOfGreaterImportanceToTheUser() throws Exception {
		given(aspect).indexPageRequires("appns/Class1")
			.and(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(aspect).classRequires("appns/Class1", "./Class2")
			.and(aspect).containsResourceFileWithContents("config.xml", "'appns/Class2'");
		when(brjs).runCommand("dep-insight", "app", "appns/Class2");
		then(output).containsText(
			"Source module 'appns/Class2' dependencies found:",
			"    +--- 'default-aspect/src/appns/Class2.js' (*)",
			"    |    \\--- 'default-aspect/resources/config.xml' (seed file)",
			"    |    |    \\--- 'default-aspect/src/appns/Class1.js' (*)",
			"    |    |    |    \\--- 'default-aspect/index.html' (seed file)");
	}
	
	@Test
	public void byDefaultDependenciesAreOnlyShownTheFirstTimeTheyAreEncountered() throws Exception {
		given(aspect).indexPageRequires("appns/Class1")
			.and(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(aspect).classRequires("appns/Class1", "./Class2")
			.and(aspect).classRequires("appns/Class2", "./Class3")
			.and(aspect).classRequires("appns/Class3", "./Class1");
		when(brjs).runCommand("dep-insight", "app", "appns/Class3");
		then(output).containsText(
			"Source module 'appns/Class3' dependencies found:",
			"    +--- 'default-aspect/src/appns/Class3.js' (*)",
			"    |    \\--- 'default-aspect/src/appns/Class2.js'",
			"    |    |    \\--- 'default-aspect/src/appns/Class1.js'",
			"    |    |    |    \\--- 'default-aspect/index.html' (seed file)",
			"",
			"    (*) - subsequent instances not shown (use -A or --all to show)");
	}
	
	@Test
	public void whenUsingTheAllSwitchIfTheSameAssetIsFoundTwiceThenItsDependenciesAreOnlyShownTheFirstTime() throws Exception {
		given(aspect).indexPageRequires("appns/Class1")
			.and(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(aspect).classRequires("appns/Class1", "./Class2")
			.and(aspect).classRequires("appns/Class2", "./Class3")
			.and(aspect).classRequires("appns/Class3", "./Class1");
		when(brjs).runCommand("dep-insight", "app", "appns/Class3", "--all");
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
			.and(aspect).hasClasses("appns/Class1", "appns/Class2", "appns/pkg/InnerClass")
			.and(aspect).classRequires("appns/Class1", "./pkg/InnerClass")
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
	
	@Test
	public void requirePrefixDependenciesAreCorrectlyShown() throws Exception {
		given(aspect).indexPageRequires("appns/pkg1/ClassA")
			.and(aspect).hasClasses("appns/pkg1/ClassA", "appns/pkg1/ClassB", "appns/pkg1/UnbundledClass", "appns/pkg2/ClassC")
			.and(aspect).classRequires("appns/pkg1/ClassA", "../pkg2/ClassC")
			.and(aspect).classRequires("appns/pkg2/ClassC", "../pkg1/ClassB");
		when(brjs).runCommand("dep-insight", "app", "appns/pkg1", "--prefix", "--all");
		then(output).containsText(
			"Require path prefix 'appns/pkg1' dependencies found:",
			"    +--- 'default-aspect/src/appns/pkg1/ClassA.js'",
			"    |    \\--- 'default-aspect/index.html' (seed file)",
			"    +--- 'default-aspect/src/appns/pkg1/ClassB.js'",
			"    |    \\--- 'default-aspect/src/appns/pkg2/ClassC.js'",
			"    |    |    \\--- 'default-aspect/src/appns/pkg1/ClassA.js' (*)",
			"",
			"    (*) - dependencies omitted (listed previously)");
	}
	
	@Test
	public void aliasedDependenciesAreCorrectlyDisplayed() throws Exception {
		given(aspect).indexPageHasAliasReferences("alias-ref")
			.and(aliasesFile).hasAlias("alias-ref", "appns.Class")
			.and(aspect).hasClass("appns/Class");
		when(brjs).runCommand("dep-insight", "app", "appns/Class");
		then(output).containsText(
			"Source module 'appns/Class' dependencies found:",
			"    +--- 'default-aspect/src/appns/Class.js'",
			"    |    \\--- 'alias!alias-ref' (alias dep.)",
			"    |    |    \\--- 'default-aspect/index.html' (seed file)");
	}
	
	@Test
	public void weCanShowDependenciesForAnAliasToo() throws Exception {
		given(aspect).indexPageHasAliasReferences("alias-ref")
			.and(aliasesFile).hasAlias("alias-ref", "appns.Class")
			.and(aspect).hasClass("appns/Class");
		when(brjs).runCommand("dep-insight", "app", "alias-ref", "--alias");
		then(output).containsText(
			"Alias 'alias-ref' dependencies found:",
			"    +--- 'default-aspect/src/appns/Class.js'",
			"    |    \\--- 'alias!alias-ref' (alias dep.)",
			"    |    |    \\--- 'default-aspect/index.html' (seed file)");
	}
	
	@Test
	public void anAliasNameWithASpaceIsntMistakenlyRecognizedAsAnAspect() throws Exception {
		given(aspect).indexPageHasAliasReferences("alias ref")
			.and(aliasesFile).hasAlias("alias ref", "appns.Class")
			.and(aspect).hasClass("appns/Class");
		when(brjs).runCommand("dep-insight", "app", "alias ref", "--alias");
		then(output).containsText(
			"Alias 'alias ref' dependencies found:",
			"    +--- 'default-aspect/src/appns/Class.js'",
			"    |    \\--- 'alias!alias ref' (alias dep.)",
			"    |    |    \\--- 'default-aspect/index.html' (seed file)");
	}
	
	@Ignore
	@Test
	public void dependenciesCanBeShownForAnIncompleteAlias() throws Exception {
		given(aspect).indexPageHasAliasReferences("appns.bs.b1.alias-ref")
			.and(aspect).hasClasses("appns.TheClass", "appns.TheInterface")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.alias-ref", null, "appns.TheInterface");
		when(brjs).runCommand("dep-insight", "app", "appns.bs.b1.alias-ref", "--alias");
		then(output).containsText(
			"Alias 'appns.bs.b1.alias-ref' dependencies found:",
			"    +--- 'default-aspect/src/appns/TheInterface.js'",
			"    |    \\--- 'alias!appns.bs.b1.alias-ref' (alias dep.)",
			"    |    |    \\--- 'default-aspect/index.html' (seed file)");
	}
	
	@Test
	public void dependenciesCanBeShownForAnIncompleteAliasThatIsntUsedWithinTheApp() throws Exception {
		given(aspect).hasClass("appns/TheInterface")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.alias-ref", null, "appns.TheInterface");
		when(brjs).runCommand("dep-insight", "app", "appns.bs.b1.alias-ref", "--alias");
		then(output).containsText(
			"Alias 'appns.bs.b1.alias-ref' dependencies found:",
			"    +--- 'default-aspect/src/appns/TheInterface.js'",
			"    |    \\--- 'alias!appns.bs.b1.alias-ref' (alias dep.)");
	}
	
	@Test
	public void requestingDependenciesForANonExistentAliasProvidesANiceMessage() throws Exception {
		given(aspect).hasBeenCreated();
		when(brjs).runCommand("dep-insight", "app", "alias-ref", "--alias");
		then(output).containsText(
			"Alias 'alias-ref' has not been defined within '" + aliasesFile.getUnderlyingFile().getPath() + "' or any other files that it inherits from");
	}
	
	@Test
	public void requestingDependenciesForAnAliasThatPointsToANonExistentSourceModuleProvidesANiceMessage() throws Exception {
		given(aliasesFile).hasAlias("alias-ref", "NonExistentClass");
		when(brjs).runCommand("dep-insight", "app", "alias-ref", "--alias");
		then(output).containsText(
			"Source file 'NonExistentClass' could not be found.");
	}
}
