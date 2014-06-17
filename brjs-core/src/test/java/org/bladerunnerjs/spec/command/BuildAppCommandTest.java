package org.bladerunnerjs.spec.command;

import static org.bladerunnerjs.plugin.plugins.commands.standard.BuildAppCommand.Messages.*;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.DirectoryAlreadyExistsCommandException;
import org.bladerunnerjs.model.exception.command.DirectoryDoesNotExistCommandException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class BuildAppCommandTest extends SpecTest {
	App app;
	Aspect aspect;
	Bladeset bladeset;
	Blade blade;
	Blade badBlade;
	private App otherApp;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsCommandPlugins()
			.and(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			aspect = app.aspect("default");
			otherApp = brjs.app("other-app");
			bladeset = app.bladeset("bladeset");
			blade = bladeset.blade("blade");
			badBlade = bladeset.blade("!$%$^");
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooFewArguments() throws Exception {
		when(brjs).runCommand("build-app");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'app-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAppDoesntExist() throws Exception {
		when(brjs).runCommand("build-app", "app");
		then(exceptions).verifyException(NodeDoesNotExistException.class, "app", unquoted(app.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfAnInvalidTargetDirIsProvided() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("build-app", "app", "target");
		then(exceptions).verifyException(DirectoryDoesNotExistCommandException.class, "target")
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void appIsExportedToGeneratedExportedAppsDirByDefault() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("build-app", "app");
		then(brjs).hasDir("generated/built-apps/app")
			.and(output).containsLine(APP_BUILT_CONSOLE_MSG, "app", brjs.file("generated/built-apps/app").getCanonicalPath());
	}
	
	@Test
	public void appOverwritesExistingBuiltAppIfBuildingToTheDefaultLocation() throws Exception {
		given(app).hasBeenCreated()
			.and(brjs).commandHasBeenRun("build-app", "app");
		when(brjs).runCommand("build-app", "app");
		then(brjs).hasDir("generated/built-apps/app")
			.and(output).containsLine(APP_BUILT_CONSOLE_MSG, "app", brjs.file("generated/built-apps/app").getCanonicalPath())
			.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void appOverwritesExistingWarIfBuildingToTheDefaultLocation() throws Exception {
		given(app).hasBeenCreated()
			.and(brjs).commandHasBeenRun("build-app", "app", "-w");
		when(brjs).runCommand("build-app", "app", "-w");
		then(brjs).hasFile("generated/built-apps/app.war")
			.and(output).containsLine(APP_BUILT_CONSOLE_MSG, "app", brjs.file("generated/built-apps/app.war").getCanonicalPath())
			.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void buildingAWarShouldNotDeleteAPreviousBuildStaticApp() throws Exception {
		given(app).hasBeenCreated()
			.and(brjs).commandHasBeenRun("build-app", "app");
		when(brjs).runCommand("build-app", "app", "-w");
		then(brjs).hasDir("generated/built-apps/app")
			.and(brjs).hasFile("generated/built-apps/app.war")
			.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void buildingAStaticAppShouldNotDeleteAPreviouslyBuiltWar() throws Exception {
		given(app).hasBeenCreated()
			.and(brjs).commandHasBeenRun("build-app", "app", "-w");
		when(brjs).runCommand("build-app", "app");
		then(brjs).hasDir("generated/built-apps/app")
			.and(brjs).hasFile("generated/built-apps/app.war")
			.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void overwrittenAppsInDefaultLocationDontNukeOtherBuiltApps() throws Exception {
		given(app).hasBeenCreated()
			.and(otherApp).hasBeenCreated()
			.and(brjs).commandHasBeenRun("build-app", "other-app")
			.and(brjs).commandHasBeenRun("build-app", "app");
		when(brjs).runCommand("build-app", "app");
		then(brjs).hasDir("generated/built-apps/other-app")
			.and(brjs).hasDir("generated/built-apps/app");
	}
	
	@Test
	public void appCanBeExportedToASpecifiedDirectory() throws Exception {
		given(app).hasBeenCreated()
			.and(brjs).hasDir("sdk/target");
		when(brjs).runCommand("build-app", "app", "target");
		then(brjs).hasDir("sdk/target/app")
			.and(output).containsLine(APP_BUILT_CONSOLE_MSG, "app", brjs.file("sdk/target/app").getCanonicalPath());
	}
	
	@Test
	public void appDoesntOverwriteExistingBuiltAppIfBuildingToACustomLocation() throws Exception {
		given(app).hasBeenCreated()
			.and(brjs).hasDir("sdk/target")
			.and(brjs).commandHasBeenRun("build-app", "app", "target");
		when(brjs).runCommand("build-app", "app", "target");
		then(exceptions).verifyException(DirectoryAlreadyExistsCommandException.class, brjs.file("sdk/target/app").getCanonicalPath())
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void appCanBeExportedToASpecifiedAbsoluteDirectory() throws Exception {
		given(app).hasBeenCreated()
			.and(brjs).hasDir("sdk/target");
		when(brjs).runCommand("build-app", "app", brjs.file("sdk/target").getAbsolutePath());
		then(brjs).hasDir("sdk/target/app")
			.and(output).containsLine(APP_BUILT_CONSOLE_MSG, "app", brjs.file("sdk/target/app").getCanonicalPath());
	}
	
	@Test
	public void appCanBeExportedAsAWar() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("build-app", "app", "-w");
		then(brjs).doesNotHaveDir("sdk/app")
			.and(brjs).hasFile("generated/built-apps/app.war")
			.and(output).containsLine(APP_BUILT_CONSOLE_MSG, "app", brjs.file("generated/built-apps/app.war").getCanonicalPath());
	}
	
	@Test
	public void appWithThemedDefaultAspectCanBeExportedAsAWar() throws Exception {
		given(brjs).usesProductionTemplates()
			.and(brjs.appJars()).containsFile("some-jar.jar")
			.and(brjs).commandHasBeenRun("create-app", "app")
			.and(aspect).containsFileWithContents("themes/standard/style.css", "ASPECT theme content")
			.and(brjs.sdkLibsDir()).containsFileWithContents("locale-forwarder.js", "Locale Forwarder");
		when(brjs).runCommand("build-app", "app", "-w");
		then(brjs).doesNotHaveDir("sdk/app")
			.and(brjs).hasFile("generated/built-apps/app.war")
			.and(output).containsLine(APP_BUILT_CONSOLE_MSG, "app", brjs.file("generated/built-apps/app.war").getCanonicalPath());
	}	
	
	@Test
	public void webXmlDevEnvironmentIsFiltered_StaticExport() throws Exception {
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("WEB-INF/web.xml", "<web-xml><!-- start-env: dev --><dev-config /><!-- end-env --></web-xml>");
		when(brjs).runCommand("build-app", "app");
		then(brjs).fileContentsDoesNotContain("generated/built-apps/app/WEB-INF/web.xml", "dev-config")
    		.and(brjs).fileContentsDoesNotContain("generated/built-apps/app/WEB-INF/web.xml", "start-env")
    		.and(brjs).fileContentsDoesNotContain("generated/built-apps/app/WEB-INF/web.xml", "end-env");
	}
	
	@Test
	public void webXmlDevEnvironmentIsFiltered_WarExport() throws Exception {
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("WEB-INF/web.xml", "<web-xml><!-- start-env: dev --><dev-config /><!-- end-env --></web-xml>");
		when(brjs).runCommand("build-app", "app", "-w")
			.and(brjs).zipFileIsExtractedTo("generated/built-apps/app.war", "generated/built-apps/app.war.exploded");
		then(brjs).fileContentsDoesNotContain("generated/built-apps/app.war.exploded/WEB-INF/web.xml", "dev-config")
    		.and(brjs).fileContentsDoesNotContain("generated/built-apps/app.war.exploded/WEB-INF/web.xml", "start-env")
    		.and(brjs).fileContentsDoesNotContain("generated/built-apps/app.war.exploded/WEB-INF/web.xml", "end-env");
	}
	
	@Test
	public void webXmlProdEnvironmentIsEnabled_StaticExport() throws Exception {
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("WEB-INF/web.xml", "<web-xml><!-- start-env: prod\n"+"<prod-config />\n"+"end-env --></web-xml>");
		when(brjs).runCommand("build-app", "app");
		then(brjs).fileContentsContains("generated/built-apps/app/WEB-INF/web.xml", "<prod-config")
    		.and(brjs).fileContentsDoesNotContain("generated/built-apps/app/WEB-INF/web.xml", "start-env")
    		.and(brjs).fileContentsDoesNotContain("generated/built-apps/app/WEB-INF/web.xml", "end-env");
	}
	
	@Test
	public void webXmlProdEnvironmentIsEnabled_WarExport() throws Exception {
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("WEB-INF/web.xml", "<web-xml><!-- start-env: prod\n"+"<prod-config />\n"+"end-env --></web-xml>");
    	when(brjs).runCommand("build-app", "app", "-w")
    		.and(brjs).zipFileIsExtractedTo("generated/built-apps/app.war", "generated/built-apps/app.war.exploded");
    	then(brjs).fileContentsContains("generated/built-apps/app.war.exploded/WEB-INF/web.xml", "<prod-config")
    		.and(brjs).fileContentsDoesNotContain("generated/built-apps/app.war.exploded/WEB-INF/web.xml", "start-env")
    		.and(brjs).fileContentsDoesNotContain("generated/built-apps/app.war.exploded/WEB-INF/web.xml", "end-env");
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(app).hasBeenCreated();
		when(brjs).runCommand("build-app", "app");
		then(exceptions).verifyNoOutstandingExceptions();
	}
}
