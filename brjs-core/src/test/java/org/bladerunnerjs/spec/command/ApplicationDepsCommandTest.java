package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.SdkJsLib;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.plugin.plugins.commands.standard.ApplicationDepsCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class ApplicationDepsCommandTest extends SpecTest {
	App app;
	Aspect aspect;
	AliasesFile aliasesFile;
	AliasDefinitionsFile bladeAliasDefinitionsFile;
	SdkJsLib brLib;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommandPlugins(new ApplicationDepsCommand())
			.and(brjs).automaticallyFindsAssetLocationPlugins()
			.and(brjs).automaticallyFindsAssetPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			aspect = app.aspect("default");
			aliasesFile = aspect.aliasesFile();
			bladeAliasDefinitionsFile = app.bladeset("bs").blade("b1").assetLocation("src").rootAliasDefinitionsFile();
			brLib = brjs.sdkLib("br");
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooFewArguments() throws Exception {
		when(brjs).runCommand("app-deps");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'app-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception {
		when(brjs).runCommand("app-deps", "a", "b", "c");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: c"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAppDoesntExist() throws Exception {
		when(brjs).runCommand("app-deps", "app", "aspect");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted(app.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheDefaultAspectDoesntExist() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("app-deps", "app");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted(aspect.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheNamedAspectDoesntExist() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("app-deps", "app", "aspect");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted(aspect.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(aspect).hasBeenCreated();
		when(brjs).runCommand("app-deps", "app");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void dependenciesAreShownWhenAllArgumentsAreValid() throws Exception {
		given(aspect).indexPageRequires("appns/Class1")
			.and(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(aspect).classRequires("appns/Class1", "./Class2");
		when(brjs).runCommand("app-deps", "app");
		then(logging).containsConsoleText(
			"Aspect 'default' dependencies found:",
			"    +--- 'default-aspect/index.html' (seed file)",
			"    |    \\--- 'default-aspect/src/appns/Class1.js'",
			"    |    |    \\--- 'default-aspect/src/appns/Class2.js'");
	}
	
	@Test
	public void staticDependenciesAreIndicatedInTheReport() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).classExtends("appns.Class1", "appns.Class2");
		when(brjs).runCommand("app-deps", "app");
		then(logging).containsConsoleText(
			"Aspect 'default' dependencies found:",
			"    +--- 'default-aspect/index.html' (seed file)",
			"    |    \\--- 'default-aspect/src/appns/Class1.js'",
			"    |    |    \\--- 'default-aspect/src/appns/Class2.js' (static dep.)");
	}
	
	@Test
	public void ifTheSameAssetIsFoundTwiceThenOnlyTheFirstEncounteredInstanceIsShownByDefault() throws Exception {
		given(aspect).indexPageRequires("appns/Class1")
			.and(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(aspect).classRequires("appns/Class1", "./Class2")
			.and(aspect).containsResourceFileWithContents("config.xml", "'appns/Class1'");
		when(brjs).runCommand("app-deps", "app");
		then(logging).containsConsoleText(
			"Aspect 'default' dependencies found:",
			"    +--- 'default-aspect/index.html' (seed file)",
			"    |    \\--- 'default-aspect/src/appns/Class1.js' (*)",
			"    |    |    \\--- 'default-aspect/src/appns/Class2.js'",
			"    |    |    |    \\--- 'default-aspect/resources/config.xml' (seed file) (*)",
			"",
			"    (*) - subsequent instances not shown (use -A or --all to show)");
	}
	
	@Test
	public void withTheAllSwitchIfTheSameAssetIsFoundTwiceThenItsDependenciesAreOnlyShownTheFirstTime() throws Exception {
		given(aspect).indexPageRequires("appns/Class1")
			.and(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(aspect).classRequires("appns/Class1", "./Class2")
			.and(aspect).containsResourceFileWithContents("config.xml", "'appns/Class1'");
		when(brjs).runCommand("app-deps", "app", "--all");
		then(logging).containsConsoleText(
			"Aspect 'default' dependencies found:",
			"    +--- 'default-aspect/index.html' (seed file)",
			"    |    \\--- 'default-aspect/src/appns/Class1.js'",
			"    |    |    \\--- 'default-aspect/src/appns/Class2.js'",
			"    |    |    |    \\--- 'default-aspect/resources/config.xml' (seed file)",
			"    |    |    |    |    \\--- 'default-aspect/src/appns/Class1.js' (*)",
			"    |    |    \\--- 'default-aspect/resources/config.xml' (seed file) (*)",
			"    +--- 'default-aspect/resources/config.xml' (seed file) (*)",
			"",
			"    (*) - dependencies omitted (listed previously)");
	}
	
	@Test
	public void weDontShowADependencyOmittedMessageForAssetsThatDontHaveDependencies() throws Exception {
		given(aspect).indexPageHasContent("'appns/Class1' & 'appns/Class2'")
			.and(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(aspect).classRequires("appns/Class1", "./Class2");
		when(brjs).runCommand("app-deps", "app");
		then(logging).containsConsoleText(
			"Aspect 'default' dependencies found:",
			"    +--- 'default-aspect/index.html' (seed file)",
			"    |    \\--- 'default-aspect/src/appns/Class1.js'",
			"    |    |    \\--- 'default-aspect/src/appns/Class2.js' (*)",
			"",
			"    (*) - subsequent instances not shown (use -A or --all to show)")			
			.and(logging).doesNotcontainConsoleText("(*) - dependencies omitted (listed previously)");
	}

	@Test
	public void weDoShowASubsequentInstanceNotShownMessageForAssetsThatDontHaveDependencies() throws Exception {
		given(aspect).indexPageHasContent("'appns/Class1' & 'appns/Class2'")
			.and(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(aspect).classRequires("appns/Class1", "./Class2");
		when(brjs).runCommand("app-deps", "app", "-A");
		then(logging).containsConsoleText(
			"Aspect 'default' dependencies found:",
			"    +--- 'default-aspect/index.html' (seed file)",
			"    |    \\--- 'default-aspect/src/appns/Class1.js'",
			"    |    |    \\--- 'default-aspect/src/appns/Class2.js'",
			"    |    \\--- 'default-aspect/src/appns/Class2.js' (*)")
			.and(logging).doesNotcontainConsoleText("subsequent instances not shown (use -A or --all to show)");
	}
	
	@Test
	public void dependenciesThatOccurDueToRelatedResourcesAreShownCorrectly() throws Exception {
		given(aspect).indexPageRequires("appns/Class1")
			.and(aspect).hasClasses("appns/Class1", "appns/Class2", "appns/pkg/NestedClass")
			.and(aspect).classRequires("appns/Class1", "./pkg/NestedClass")
			.and(aspect).containsFileWithContents("src/appns/pkg/config.xml", "'appns/Class2'")
			.and(aspect).containsEmptyFile("src/pkg/empty-config.xml");
		when(brjs).runCommand("app-deps", "app");
		then(logging).containsConsoleText(
			"Aspect 'default' dependencies found:",
			"    +--- 'default-aspect/index.html' (seed file)",
			"    |    \\--- 'default-aspect/src/appns/Class1.js'",
			"    |    |    \\--- 'default-aspect/src/appns/pkg/NestedClass.js'",
			"    |    |    |    \\--- 'default-aspect/src/appns/pkg/config.xml' (implicit resource)",
			"    |    |    |    |    \\--- 'default-aspect/src/appns/Class2.js'");
	}
	
	@Test
	public void dependenciesThatOccurDueToNonImmediateRelatedResourcesAreShownCorrectly() throws Exception {
		given(aspect).indexPageRequires("appns/Class1")
			.and(aspect).hasClasses("appns/Class1", "appns/Class2", "appns/pkg1/pkg2/NestedClass")
			.and(aspect).classRequires("appns/Class1", "./pkg1/pkg2/NestedClass")
			.and(aspect).containsFileWithContents("src/appns/pkg1/config.xml", "'appns/Class2'");
		when(brjs).runCommand("app-deps", "app");
		then(logging).containsConsoleText(
			"Aspect 'default' dependencies found:",
			"    +--- 'default-aspect/index.html' (seed file)",
			"    |    \\--- 'default-aspect/src/appns/Class1.js'",
			"    |    |    \\--- 'default-aspect/src/appns/pkg1/pkg2/NestedClass.js'",
			"    |    |    |    \\--- 'default-aspect/src/appns/pkg1/config.xml' (implicit resource)",
			"    |    |    |    |    \\--- 'default-aspect/src/appns/Class2.js'");
	}
	
	@Test
	public void aliasedDependenciesFromTheIndexPageAreCorrectlyDisplayed() throws Exception {
		given(aspect).indexPageHasAliasReferences("alias-ref")
			.and(aliasesFile).hasAlias("alias-ref", "appns.Class")
			.and(aspect).hasClass("appns/Class");
		when(brjs).runCommand("app-deps", "app");
		then(logging).containsConsoleText(
			"Aspect 'default' dependencies found:",
			"    +--- 'default-aspect/index.html' (seed file)",
			"    |    \\--- 'alias!alias-ref' (alias dep.)",
			"    |    |    \\--- 'default-aspect/src/appns/Class.js'");
	}
	
	@Test
	public void aliasedDependenciesFromAClassAreCorrectlyDisplayed() throws Exception {
		// TODO: switch to require style once they support aliases
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).classDependsOnAlias("appns.Class1", "alias-ref")
			.and(aliasesFile).hasAlias("alias-ref", "appns.Class2");
		when(brjs).runCommand("app-deps", "app");
		then(logging).containsConsoleText(
			"Aspect 'default' dependencies found:",
			"    +--- 'default-aspect/index.html' (seed file)",
			"    |    \\--- 'default-aspect/src/appns/Class1.js'",
			"    |    |    \\--- 'alias!alias-ref' (alias dep.)",
			"    |    |    |    \\--- 'default-aspect/src/appns/Class2.js'");
	}
	
	@Test
	public void incompleteAliasedDependenciesAreCorrectlyDisplayed() throws Exception {
		given(brLib).hasClass("br/UnknownClass")
			.and(aspect).indexPageHasAliasReferences("appns.bs.b1.alias-ref")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.alias-ref", null, "appns.Interface")
			.and(aspect).hasClasses("appns/Class", "appns/Interface");
		when(brjs).runCommand("app-deps", "app");
		then(logging).containsConsoleText(
			"Aspect 'default' dependencies found:",
			"    +--- 'default-aspect/index.html' (seed file)",
			"    |    \\--- 'alias!appns.bs.b1.alias-ref' (alias dep.)",
			"    |    |    \\--- '../../libs/javascript/br/src/br/UnknownClass.js'");
	}
	
	@Test
	public void dependenciesCanInvolveARelatedResourceThatRefersToAnAlias() throws Exception {
		given(aspect).indexPageRequires("appns/Class1")
			.and(aspect).hasClasses("appns/Class1", "appns/Class2", "appns/pkg/NestedClass")
			.and(aspect).classRequires("appns/Class1", "./pkg/NestedClass")
			.and(aspect).containsFileWithContents("src/appns/pkg/config.xml", "'alias-ref'")
			.and(aliasesFile).hasAlias("alias-ref", "appns.Class2");
		when(brjs).runCommand("app-deps", "app");
		then(logging).containsConsoleText(
			"Aspect 'default' dependencies found:",
			"    +--- 'default-aspect/index.html' (seed file)",
			"    |    \\--- 'default-aspect/src/appns/Class1.js'",
			"    |    |    \\--- 'default-aspect/src/appns/pkg/NestedClass.js'",
			"    |    |    |    \\--- 'default-aspect/src/appns/pkg/config.xml' (implicit resource)",
			"    |    |    |    |    \\--- 'alias!alias-ref' (alias dep.)",
			"    |    |    |    |    |    \\--- 'default-aspect/src/appns/Class2.js'");
	}
}
