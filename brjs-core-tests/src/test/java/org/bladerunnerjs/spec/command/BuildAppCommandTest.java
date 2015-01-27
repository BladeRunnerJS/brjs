package org.bladerunnerjs.spec.command;

import static org.bladerunnerjs.plugin.commands.standard.BuildAppCommand.Messages.*;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.DirectoryDoesNotExistCommandException;
import org.bladerunnerjs.api.model.exception.command.DirectoryNotEmptyCommandException;
import org.bladerunnerjs.api.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class BuildAppCommandTest extends SpecTest
{

	App app;
	Aspect defaultAspect;
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
		defaultAspect = app.defaultAspect();
		otherApp = brjs.app("other-app");
		bladeset = app.bladeset("bladeset");
		blade = bladeset.blade("blade");
		badBlade = bladeset.blade("!$%$^");
	}

	@Test
	public void exceptionIsThrownIfThereAreTooFewArguments() throws Exception
	{
		when(brjs).runCommand("build-app");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'app-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}

	@Test
	public void exceptionIsThrownIfTheAppDoesntExist() throws Exception
	{
		when(brjs).runCommand("build-app", "app");
		then(exceptions).verifyException(NodeDoesNotExistException.class, "app", unquoted(app.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}

	@Test
	public void exceptionIsThrownIfAnInvalidTargetDirIsProvided() throws Exception
	{
		given(app).hasBeenCreated();
		when(brjs).runCommand("build-app", "app", "target");
		then(exceptions).verifyException(DirectoryDoesNotExistCommandException.class, "target")
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}

	@Test
	public void appIsExportedToGeneratedExportedAppsDirByDefault() throws Exception
	{
		given(app).hasBeenCreated();
		when(brjs).runCommand("build-app", "app");
		then(brjs).hasDir("generated/built-apps/app")
			.and(logging).containsFormattedConsoleMessage(APP_BUILT_CONSOLE_MSG, "app", brjs.file("generated/built-apps/app").getAbsolutePath());
	}

	@Test
	public void appOverwritesExistingBuiltAppIfBuildingToTheDefaultLocation() throws Exception
	{
		given(app).hasBeenCreated().and(brjs).commandHasBeenRun("build-app", "app");
		when(brjs).runCommand("build-app", "app");
		then(brjs).hasDir("generated/built-apps/app")
			.and(logging).containsFormattedConsoleMessage(APP_BUILT_CONSOLE_MSG, "app", brjs.file("generated/built-apps/app").getAbsolutePath())
			.and(exceptions).verifyNoOutstandingExceptions();
	}

	@Test
	public void appOverwritesExistingWarIfBuildingToTheDefaultLocation() throws Exception
	{
		given(app).hasBeenCreated().and(brjs).commandHasBeenRun("build-app", "app", "-w");
		when(brjs).runCommand("build-app", "app", "-w");
		then(brjs).hasFile("generated/built-apps/app.war")
			.and(logging).containsFormattedConsoleMessage(APP_BUILT_CONSOLE_MSG, "app", brjs.file("generated/built-apps/app.war").getAbsolutePath())
			.and(exceptions).verifyNoOutstandingExceptions();
	}

	@Test
	public void buildingAWarShouldNotDeleteAPreviousBuildStaticApp() throws Exception
	{
		given(app).hasBeenCreated().and(brjs).commandHasBeenRun("build-app", "app");
		when(brjs).runCommand("build-app", "app", "-w");
		then(brjs).hasDir("generated/built-apps/app")
			.and(brjs).hasFile("generated/built-apps/app.war")
			.and(exceptions).verifyNoOutstandingExceptions();
	}

	@Test
	public void buildingAStaticAppShouldNotDeleteAPreviouslyBuiltWar() throws Exception
	{
		given(app).hasBeenCreated().and(brjs).commandHasBeenRun("build-app", "app", "-w");
		when(brjs).runCommand("build-app", "app");
		then(brjs).hasDir("generated/built-apps/app")
			.and(brjs).hasFile("generated/built-apps/app.war")
			.and(exceptions).verifyNoOutstandingExceptions();
	}

	@Test
	public void overwrittenAppsInDefaultLocationDontNukeOtherBuiltApps() throws Exception
	{
		given(app).hasBeenCreated().and(otherApp).hasBeenCreated().and(brjs).commandHasBeenRun("build-app", "other-app").and(brjs).commandHasBeenRun("build-app", "app");
		when(brjs).runCommand("build-app", "app");
		then(brjs).hasDir("generated/built-apps/other-app")
			.and(brjs).hasDir("generated/built-apps/app");
	}

	@Test
	public void appCanBeExportedToASpecifiedDirectory() throws Exception
	{
		given(app).hasBeenCreated().and(brjs).hasDir("sdk/target");
		when(brjs).runCommand("build-app", "app", "target");
		then(brjs).hasDir("sdk/target")
			.and(logging).containsFormattedConsoleMessage(APP_BUILT_CONSOLE_MSG, "app", brjs.file("sdk/target").getAbsolutePath());
	}

	@Test
	public void appDoesntOverwriteExistingBuiltAppIfBuildingToACustomLocation() throws Exception
	{
		given(app).hasBeenCreated()
			.and(app.defaultAspect()).indexPageHasContent("index page")
			.and(brjs).localeForwarderHasContents("locale-forwarder.js")
			.and(brjs).hasDir("sdk/target")
			.and(brjs).commandHasBeenRun("build-app", "app", "target");
		when(brjs).runCommand("build-app", "app", "target");
		then(exceptions).verifyException(DirectoryNotEmptyCommandException.class, brjs.file("sdk/target").getAbsolutePath())
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}

	@Test
	public void appCanBeExportedToASpecifiedAbsoluteDirectory() throws Exception
	{
		given(app).hasBeenCreated().and(brjs).hasDir("sdk/target");
		when(brjs).runCommand("build-app", "app", brjs.file("sdk/target").getAbsolutePath());
		then(brjs).hasDir("sdk/target")
			.and(logging).containsFormattedConsoleMessage(APP_BUILT_CONSOLE_MSG, "app", brjs.file("sdk/target").getAbsolutePath());
	}

	@Test
	public void appCanBeExportedAsAWar() throws Exception
	{
		given(app).hasBeenCreated();
		when(brjs).runCommand("build-app", "app", "-w");
		then(brjs).doesNotHaveDir("sdk/app").and(brjs).hasFile("generated/built-apps/app.war")
			.and(logging).containsFormattedConsoleMessage(APP_BUILT_CONSOLE_MSG, "app", brjs.file("generated/built-apps/app.war").getAbsolutePath());
	}

	@Test
	public void appWithThemedDefaultAspectCanBeExportedAsAWar() throws Exception
	{
		given(brjs).usesProductionTemplates()
			.and(brjs.appJars()).containsFile("some-jar.jar")
			.and(brjs).commandHasBeenRun("create-app", "app")
			.and(defaultAspect).containsFileWithContents("themes/standard/style.css", "ASPECT theme content")
			.and(brjs).localeForwarderHasContents("locale-forwarder.js");
		when(brjs).runCommand("build-app", "app", "-w");
		then(brjs).doesNotHaveDir("sdk/app")
			.and(brjs).hasFile("generated/built-apps/app.war")
			.and(logging).containsFormattedConsoleMessage(APP_BUILT_CONSOLE_MSG, "app", brjs.file("generated/built-apps/app.war").getAbsolutePath());
	}

	@Test
	public void webXmlDevEnvironmentIsFiltered_StaticExport() throws Exception
	{
		given(app).hasBeenCreated().and(app).containsFileWithContents("WEB-INF/web.xml", "<web-xml><!-- start-env: dev --><dev-config /><!-- end-env --></web-xml>");
		when(brjs).runCommand("build-app", "app");
		then(brjs).fileContentsDoesNotContain("generated/built-apps/app/WEB-INF/web.xml", "dev-config")
			.and(brjs).fileContentsDoesNotContain("generated/built-apps/app/WEB-INF/web.xml", "start-env")
			.and(brjs).fileContentsDoesNotContain("generated/built-apps/app/WEB-INF/web.xml", "end-env");
	}

	@Test
	public void webXmlDevEnvironmentIsFiltered_WarExport() throws Exception
	{
		given(app).hasBeenCreated().and(app).containsFileWithContents("WEB-INF/web.xml", "<web-xml><!-- start-env: dev --><dev-config /><!-- end-env --></web-xml>");
		when(brjs).runCommand("build-app", "app", "-w").and(brjs).zipFileIsExtractedTo("generated/built-apps/app.war", "generated/built-apps/app.war.exploded");
		then(brjs).fileContentsDoesNotContain("generated/built-apps/app.war.exploded/WEB-INF/web.xml", "dev-config")
			.and(brjs).fileContentsDoesNotContain("generated/built-apps/app.war.exploded/WEB-INF/web.xml", "start-env")
			.and(brjs).fileContentsDoesNotContain("generated/built-apps/app.war.exploded/WEB-INF/web.xml", "end-env");
	}

	@Test
	public void webXmlProdEnvironmentIsEnabled_StaticExport() throws Exception
	{
		given(app).hasBeenCreated().and(app).containsFileWithContents("WEB-INF/web.xml", "<web-xml><!-- start-env: prod\n" + "<prod-config />\n" + "end-env --></web-xml>");
		when(brjs).runCommand("build-app", "app");
		then(brjs).fileContentsContains("generated/built-apps/app/WEB-INF/web.xml", "<prod-config")
			.and(brjs).fileContentsDoesNotContain("generated/built-apps/app/WEB-INF/web.xml", "start-env")
			.and(brjs).fileContentsDoesNotContain("generated/built-apps/app/WEB-INF/web.xml", "end-env");
	}

	@Test
	public void webXmlProdEnvironmentIsEnabled_WarExport() throws Exception
	{
		given(app).hasBeenCreated().and(app).containsFileWithContents("WEB-INF/web.xml", "<web-xml><!-- start-env: prod\n" + "<prod-config />\n" + "end-env --></web-xml>");
		when(brjs).runCommand("build-app", "app", "-w").and(brjs).zipFileIsExtractedTo("generated/built-apps/app.war", "generated/built-apps/app.war.exploded");
		then(brjs).fileContentsContains("generated/built-apps/app.war.exploded/WEB-INF/web.xml", "<prod-config")
			.and(brjs).fileContentsDoesNotContain("generated/built-apps/app.war.exploded/WEB-INF/web.xml", "start-env")
			.and(brjs).fileContentsDoesNotContain("generated/built-apps/app.war.exploded/WEB-INF/web.xml", "end-env");
	}
	
	@Test
	public void webXmlNameSpaceIsPreserved_WarExport() throws Exception
	{
		given(app).hasBeenCreated().and(app).containsFileWithContents("WEB-INF/web.xml", 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
					"<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + 
					"version=\"2.5\" xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd\">" +
				"</web-app>");
		when(brjs).runCommand("build-app", "app", "-w").and(brjs).zipFileIsExtractedTo("generated/built-apps/app.war", "generated/built-apps/app.war.exploded");
		then(brjs).fileContentsContains("generated/built-apps/app.war.exploded/WEB-INF/web.xml", "xmlns=\"http://java.sun.com/xml/ns/javaee\"");
	}

	@Test
	public void appVersionTokenIsReplaced() throws Exception
	{
		given(app).hasBeenCreated().and(app).containsFileWithContents("WEB-INF/web.xml", "<web-xml>@appVersion@</web-xml>").and(brjs).hasProdVersion("1234");
		when(brjs).runCommand("build-app", "app");
		then(brjs).fileContentsContains("generated/built-apps/app/WEB-INF/web.xml", "<web-xml>1234</web-xml>")
			.and(brjs).fileContentsDoesNotContain("generated/built-apps/app/WEB-INF/web.xml", "@appVersion@");
	}

	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated().and(app).hasBeenCreated();
		when(brjs).runCommand("build-app", "app");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void defaultAspectsAreBuiltCorrectly() throws Exception
	{
		given(app).hasBeenCreated()
			.and(brjs).localeForwarderHasContents("")
			.and(app.defaultAspect()).hasBeenCreated()
			.and(app.appConf()).supportsLocales("en_GB")
			.and(app.defaultAspect()).indexPageHasContent("DEFAULT ASPECT INDEX PAGE");
		when(brjs).runCommand("build-app", "app");
		then(brjs).fileContentsContains("generated/built-apps/app/en_GB/index.html", "DEFAULT ASPECT INDEX PAGE");
	}
	
}
