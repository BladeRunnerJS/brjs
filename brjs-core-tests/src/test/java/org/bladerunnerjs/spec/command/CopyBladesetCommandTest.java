package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.model.exception.name.InvalidPackageNameException;
import org.bladerunnerjs.plugin.plugins.commands.standard.CopyBladesetCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class CopyBladesetCommandTest extends SpecTest {
	App sourceApp;
	Bladeset sourceBladeset;
	App targetApp;
	Bladeset targetBladeset;
	Bladeset namedTargetBladeset;
	AppConf targetAppConf;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommandPlugins(new CopyBladesetCommand())
			.and(brjs).automaticallyFindsAssetLocationPlugins()
			.and(brjs).hasBeenCreated();
			sourceApp = brjs.app("app");
			sourceBladeset = sourceApp.bladeset("bs");
			targetApp = brjs.app("target-app");
			targetBladeset = targetApp.bladeset("bs");
			namedTargetBladeset = targetApp.bladeset("bs2");
			targetAppConf = targetApp.appConf();
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooFewArguments() throws Exception {
		when(brjs).runCommand("copy-bladeset");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'source-app-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception {
		when(brjs).runCommand("copy-bladeset", "a", "b", "c", "d", "e");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: e"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheSourceAppDoesntExist() throws Exception {
		when(brjs).runCommand("copy-bladeset", "no-such-app", "b", "c");
		then(exceptions).verifyException(NodeDoesNotExistException.class, "no-such-app")
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheSourceBladesetDoesntExist() throws Exception {
		given(sourceApp).hasBeenCreated();
		when(brjs).runCommand("copy-bladeset", "app", "bsx", "c");
		then(exceptions).verifyException(NodeDoesNotExistException.class, "bsx")
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheTargetAppDoesntExist() throws Exception {
		given(sourceBladeset).hasBeenCreated();
		when(brjs).runCommand("copy-bladeset", "app", "bs", "no-such-app");
		then(exceptions).verifyException(NodeDoesNotExistException.class, "no-such-app")
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheTargetBladesetAlreadyExists() throws Exception {
		given(sourceBladeset).hasBeenCreated()
			.and(targetBladeset).hasBeenCreated();
		when(brjs).runCommand("copy-bladeset", "app", "bs", "target-app");
		then(exceptions).verifyException(NodeAlreadyExistsException.class, "bs")
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheTargetBladesetNameIsInvalid() throws Exception {
		given(sourceBladeset).hasBeenCreated()
			.and(targetApp).hasBeenCreated();
		when(brjs).runCommand("copy-bladeset", "app", "bs", "target-app", "target-bladeset!");
		then(exceptions).verifyException(InvalidPackageNameException.class, "target-bladeset!")
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void bladesetIsCopiedWithTheSameBladesetNameByDefault() throws Exception {
		given(sourceBladeset).hasBeenCreated()
			.and(targetApp).hasBeenCreated();
		when(brjs).runCommand("copy-bladeset", "app", "bs", "target-app");
		then(targetBladeset).dirExists();
	}
	
	@Test
	public void bladesetIsCopiedWithTheGivenNameIfANameIsSpecified() throws Exception {
		given(sourceBladeset).hasBeenCreated()
			.and(targetApp).hasBeenCreated();
		when(brjs).runCommand("copy-bladeset", "app", "bs", "target-app", "bs2");
		then(namedTargetBladeset).dirExists();
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(sourceBladeset).hasBeenCreated()
			.and(targetApp).hasBeenCreated();
		when(brjs).runCommand("copy-bladeset", "app", "bs", "target-app");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void sourceFilesAreMovedToReflectTheTargetAppNamespace() throws Exception {
		given(sourceBladeset).hasBeenCreated()
			.and(sourceBladeset).hasClass("appns/bs/pkg/Class")
			.and(targetApp).hasBeenCreated()
			.and(targetAppConf).hasRequirePrefix("targetns");
		when(brjs).runCommand("copy-bladeset", "app", "bs", "target-app");
		then(targetBladeset).hasFile("src/targetns/bs/pkg/Class.js");
	}
	
	@Test
	public void sourceFilesAreMovedIfTheTargetBladesetNameIsDifferentToTheSourceName() throws Exception {
		given(sourceBladeset).hasBeenCreated()
			.and(sourceBladeset).hasClass("appns/bs/pkg/Class")
			.and(targetApp).hasBeenCreated();
		when(brjs).runCommand("copy-bladeset", "app", "bs", "target-app", "bs2");
		then(namedTargetBladeset).hasFile("src/appns/bs2/pkg/Class.js");
	}
	
	@Test
	public void sourceFilesAreMovedCorrectlyWhenBothTheTargetAppNamespaceAndTheTargetBladesetChange() throws Exception {
		given(sourceBladeset).hasBeenCreated()
			.and(sourceBladeset).hasClass("appns/bs/pkg/Class")
			.and(targetApp).hasBeenCreated()
			.and(targetAppConf).hasRequirePrefix("targetns");
		when(brjs).runCommand("copy-bladeset", "app", "bs", "target-app", "bs2");
		then(namedTargetBladeset).hasFile("src/targetns/bs2/pkg/Class.js");
	}
	
	@Test
	public void sourceFilesAreLeftInTheirOriginalLocationIfTheRequirePrefixIsUnchanged() throws Exception {
		given(sourceBladeset).hasClass("appns/bs/Class")
			.and(targetApp).hasBeenCreated();
		when(brjs).runCommand("copy-bladeset", "app", "bs", "target-app");
		then(targetBladeset).hasFile("src/appns/bs/Class.js");
	}
	
	@Test
	public void commonJsClassesAreUpdatedBasedOnTheNewRequirePrefix() throws Exception {
		given(sourceBladeset).hasBeenCreated()
			.and(sourceBladeset).hasClass("appns/bs/Class1")
			.and(sourceBladeset).classRequires("appns/bs/Class2", "appns/bs/Class1")
			.and(targetApp).hasBeenCreated()
			.and(targetAppConf).hasRequirePrefix("targetns");
		when(brjs).runCommand("copy-bladeset", "app", "bs", "target-app", "bs2");
		then(namedTargetBladeset).fileContentsContains("src/targetns/bs2/Class2.js", "require('targetns/bs2/Class1')");
	}
	
	@Test
	public void namespacedJsClassesAreUpdatedBasedOnTheNewRequirePrefix() throws Exception {
		given(sourceBladeset).hasNamespacedJsPackageStyle()
			.and(sourceBladeset).hasClass("appns.bs.Class")
			.and(targetApp).hasBeenCreated()
			.and(targetAppConf).hasRequirePrefix("targetns");
		when(brjs).runCommand("copy-bladeset", "app", "bs", "target-app", "bs2");
		then(namedTargetBladeset).fileContentsContains("src/targetns/bs2/Class.js", "targetns.bs2.Class = function() {");
	}
	
	@Test
	public void aJsStyleFileShouldBeAddedIfTheImportedBladesetHasADifferentStyleToWhereItsBeingImported() throws Exception {
		given(sourceApp).hasNamespacedJsPackageStyle()
			.and(sourceBladeset).hasClass("appns.bs.Class")
			.and(targetApp).hasBeenCreated();
		when(brjs).runCommand("copy-bladeset", "app", "bs", "target-app");
		then(targetBladeset).hasFile(".js-style");
	}
	
	@Test
	public void aJsStyleFileShouldNotBeAddedIfTheImportedBladesetHasTheSameStyleAsWhereItsBeingImported() throws Exception {
		given(sourceBladeset).hasClass("appns/bs/Class")
			.and(targetApp).hasBeenCreated();
		when(brjs).runCommand("copy-bladeset", "app", "bs", "target-app");
		then(targetBladeset).doesNotHaveFile(".js-style");
	}
}
