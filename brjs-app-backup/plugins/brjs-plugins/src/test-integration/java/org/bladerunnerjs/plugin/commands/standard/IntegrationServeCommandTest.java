package org.bladerunnerjs.plugin.commands.standard;

import static org.bladerunnerjs.appserver.BRJSApplicationServer.Messages.*;
import static org.bladerunnerjs.plugin.commands.standard.ServeCommand.Messages.*;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.appserver.ApplicationServer;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.TemplateGroup;
import org.bladerunnerjs.plugin.commands.standard.ServeCommand;
import org.bladerunnerjs.plugin.plugins.require.AppMetaDataSourceModule;
import org.bladerunnerjs.utility.LoggingMissingTokenHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Predicate;

public class IntegrationServeCommandTest extends SpecTest
{
	private ApplicationServer appServer;
	private TemplateGroup templates;
	private File secondaryTempFolder;
	private BRJS secondBrjsProcess;
	private App app;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();

	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommandPlugins(new ServeCommand())
			.and(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated()
			.and(brjs).containsFolder("apps")
			.and(brjs).containsFolder("sdk/system-applications");
		appServer = brjs.applicationServer(appServerPort);
		brjs.bladerunnerConf().setJettyPort(appServerPort);
		templates = brjs.sdkTemplateGroup("default");
		app = brjs.app("app1");
		aspect = app.defaultAspect();
		brjs.appJars().create();

		given(brjs).usedForServletModel();
	}
	
	@After
	public void tearDown() throws Exception
	{
		if (secondaryTempFolder != null) org.apache.commons.io.FileUtils.deleteQuietly(secondaryTempFolder);
		if (secondBrjsProcess != null) { secondBrjsProcess.close(); }
		logging.disableStoringLogs();
		logging.emptyLogStore();
		appServer = brjs.applicationServer(appServerPort);
		appServer.stop();
	}
	
	@Test
	public void serveCommandStartsAppServer() throws Exception
	{
		given(logging).enabled()
			.and(brjs).pluginsAccessed();
		when(brjs).runThreadedCommand("serve");
		then(logging).infoMessageReceived(SERVER_STARTING_LOG_MSG, "BladeRunnerJS")
			.and(logging).infoMessageReceived(BRJS.Messages.NO_APPS_DISCOVERED, "system")
			.and(logging).infoMessageReceived(BRJS.Messages.NO_APPS_DISCOVERED, "user")
			.and(logging).infoMessageReceived(SERVER_STARTED_LOG_MESSAGE, appServerPort)
			.and(logging).containsFormattedConsoleMessage(SERVER_STARTUP_MESSAGE + appServerPort +"/")
			.and(logging).containsFormattedConsoleMessage(SERVER_STOP_INSTRUCTION_MESSAGE + "\n")
			.and(appServer).requestIs302Redirected("/", "/dashboard/");
	}
	
	@Test
	public void newAppCreatedFromADifferentModelIsHostedIfAppsLiveSeperateFromTheSdk() throws Exception
	{
		secondaryTempFolder = org.bladerunnerjs.utility.FileUtils.createTemporaryDirectory(this.getClass());
		given(brjs).hasBeenAuthenticallyCreatedWithWorkingDir(secondaryTempFolder); 
			/*and*/ secondBrjsProcess = createNonTestModel(secondaryTempFolder);
			given(brjs.sdkTemplateGroup("default")).templateGroupCreated()
			.and(brjs.sdkTemplateGroup("default").template("app")).containsFile("index.html")
			.and(brjs).usedForServletModel();
		when(brjs).runThreadedCommand("serve")
			.and(secondBrjsProcess).runCommand("create-app", "app1", "blah");
		then(brjs.applicationServer(appServerPort)).requestCanEventuallyBeMadeFor("/app1/")
			.and(testRootDirectory).doesNotContainDir("apps")
			.and(testRootDirectory).doesNotContainDir("app1")
			.and(secondaryTempFolder).containsDir("app1");
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated();
			/* and */ brjs.bladerunnerConf().setJettyPort(appServerPort);
		when(brjs).runThreadedCommand("serve");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void canOverridePortValueWithArgument() throws Exception
	{
		appServerPort = 7777;
		appServer = brjs.applicationServer(appServerPort);
		
		given(logging).enabled()
			.and(brjs).pluginsAccessed();
		when(brjs).runThreadedCommand("serve", "-p", "7777");
		then(logging).infoMessageReceived(SERVER_STARTING_LOG_MSG, "BladeRunnerJS")
			.and(logging).infoMessageReceived(BRJS.Messages.NO_APPS_DISCOVERED, "system")
			.and(logging).infoMessageReceived(BRJS.Messages.NO_APPS_DISCOVERED, "user")
			.and(logging).infoMessageReceived(SERVER_STARTED_LOG_MESSAGE, "7777")
			.and(logging).containsFormattedConsoleMessage(SERVER_STARTUP_MESSAGE + "7777/")
			.and(logging).containsFormattedConsoleMessage(SERVER_STOP_INSTRUCTION_MESSAGE + "\n")
			.and(appServer).requestIs302Redirected("/", "/dashboard/");
	}
	
	@Test
	public void serverWillServeAppsOnceStarted() throws Exception
	{
		given(brjs).hasBeenAuthenticallyReCreated()
			.and(brjs).usedForServletModel()
			.and(brjs).localeSwitcherHasContents("")
			.and(templates).templateGroupCreated()
			.and(templates.template("app")).containsFile("fileForApp.txt")
			.and(brjs.app("app1")).hasBeenPopulated("default")
			.and(brjs.app("app1").appConf()).supportsLocales("en", "de");
		when(brjs).runThreadedCommand("serve");
		then(appServer).requestCanEventuallyBeMadeFor("/app1");
	}
	
	@Test
	public void serveCommandStartsTheFileWatcher() throws Exception
	{
		brjs = null;
		given(brjs).hasBeenAuthenticallyReCreated()
			.and(brjs).localeSwitcherHasContents("")
			.and(brjs).usedForServletModel();
		App app = brjs.app("app1");
		Aspect aspect = app.defaultAspect();
		appServer = brjs.applicationServer();
		
		given(aspect).hasClass("appns/Class1")
			.and(aspect).hasClass("appns/Class2")
			.and(aspect).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
				+ "locales: en\n"
				+ "requirePrefix: appns")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(app).hasReceivedRequest("v/dev/js/dev/combined/bundle.js");
		when(brjs).runThreadedCommand("serve")
			.and(aspect).indexPageRefersToWithoutNotifyingFileRegistry("appns.Class2");
		then(appServer).requestCanEventuallyBeMadeWhereResponseMatches("/app1/v/dev/js/dev/combined/bundle.js", new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return input.contains("Class2 =") && !input.contains("Class1");
			}
		});
	}
	
	@Test
	public void versionIsConfigurable() throws Exception
	{
		brjs = null;
		given(brjs).hasBeenAuthenticallyReCreated()
			.and(brjs).localeSwitcherHasContents("")
			.and(brjs).usedForServletModel();
			App app = brjs.app("app1");
    		Aspect aspect = app.defaultAspect();
    		appServer = brjs.applicationServer();
    		given(aspect).hasClass("appns/Class1")
			.and(aspect).hasClass("appns/Class2")
			.and(aspect).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
					+ "locales: en\n"
					+ "requirePrefix: appns")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).indexPageHasContent("require('" + AppMetaDataSourceModule.PRIMARY_REQUIRE_PATH + "');");
		when(brjs).runThreadedCommand("serve", "-v", "myversion");
		then(appServer).requestCanEventuallyBeMadeWhereResponseMatches("/app1/v/myversion/js/dev/combined/bundle.js", new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return input.contains("module.exports.APP_VERSION = 'myversion");
			}
		});
	}
	
	@Test
	public void customVersionHasTimestampAppended() throws Exception
	{
		brjs = null;
		given(brjs).hasBeenAuthenticallyReCreated()
			.and(brjs).localeSwitcherHasContents("")
			.and(brjs).usedForServletModel();
			App app = brjs.app("app1");
    		Aspect aspect = app.defaultAspect();
    		appServer = brjs.applicationServer();
    		given(aspect).hasClass("appns/Class1")
    		.and(aspect).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
					+ "locales: en\n"
					+ "requirePrefix: appns")
			.and(aspect).hasClass("appns/Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).indexPageHasContent("require('" + AppMetaDataSourceModule.PRIMARY_REQUIRE_PATH + "');");
		when(brjs).runThreadedCommand("serve", "-v", "myversion");
		then(appServer).requestCanEventuallyBeMadeWhereResponseMatches("/app1/v/myversion/js/dev/combined/bundle.js", new Predicate<String>()
		{
			@Override
			public boolean apply(String input)
			{
				for (String line : StringUtils.split(input, "\n")) {
					
					if (line.matches( "module\\.exports\\.APP_VERSION = 'myversion\\-[0-9]{14}';")) {
						return true;
					}
				}
				return false;
			}
		});
	}

	@Test
	public void tokensCanBeReplacedFromDefaultEnvironmentPropertiesFile() throws Exception
	{
		given(app).hasBeenCreated()
				.and(app).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
				+ "locales: en\n"
				+ "requirePrefix: appns")
				.and(app).hasDefaultEnvironmentProperties("SOME.TOKEN", "token replacement")
				.and(aspect).hasBeenCreated()
				.and(aspect).indexPageHasContent("@SOME.TOKEN@");
		when(brjs).runThreadedCommand("serve");
		then(appServer).requestForUrlReturns("/app1/", "token replacement");
	}
	
	@Test
	public void devIsTheDefaultEnvironmentIfNoneIsSpecified() throws Exception
	{
		given(app).hasBeenCreated()
				.and(app).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
				+ "locales: en\n"
				+ "requirePrefix: appns")
				.and(app).hasDefaultEnvironmentProperties("SOME.TOKEN", "token replacement")
				.and(app).hasEnvironmentProperties("dev", "SOME.TOKEN", "dev replacement")
				.and(aspect).hasBeenCreated()
				.and(aspect).indexPageHasContent("@SOME.TOKEN@");
		when(brjs).runThreadedCommand("serve");
		then(appServer).requestForUrlReturns("/app1/", "dev replacement");
	}

	@Test
	public void tokensFromPropertiesFilesCanBeReplacedInBundles() throws Exception
	{
		given(app).hasBeenCreated()
				.and(app).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
				+ "locales: en\n"
				+ "requirePrefix: appns")
				.and(app).hasDefaultEnvironmentProperties("SOME.TOKEN", "token replacement")
				.and(aspect).hasBeenCreated()
				.and(aspect).containsFileWithContents("src/App.js", "@SOME.TOKEN@")
				.and(aspect).indexPageRequires("appns/App")
				.and(brjs).hasVersion("dev");
		when(brjs).runThreadedCommand("serve");
		then(appServer).requestForUrlContains("/app1/v/dev/js/dev/combined/bundle.js", "token replacement");
	}

	@Test
	public void environmenShortFlagCanBeUsedToSetTheEnvironmentForTokens() throws Exception
	{
		given(app).hasBeenCreated()
				.and(app).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
				+ "locales: en\n"
				+ "requirePrefix: appns")
				.and(app).hasDefaultEnvironmentProperties("SOME.TOKEN", "token replacement")
				.and(app).hasEnvironmentProperties("prod", "SOME.TOKEN", "prod replacement")
				.and(aspect).hasBeenCreated()
				.and(aspect).containsFileWithContents("src/App.js", "@SOME.TOKEN@")
				.and(aspect).indexPageRequires("appns/App")
				.and(brjs).hasVersion("dev");
		when(brjs).runThreadedCommand("serve", "-e", "prod");
		then(appServer).requestForUrlContains("/app1/v/dev/js/dev/combined/bundle.js", "prod replacement");
	}

	@Test
	public void environmenLongFlagCanBeUsedToSetTheEnvironmentForTokens() throws Exception
	{
		given(app).hasBeenCreated()
				.and(app).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
				+ "locales: en\n"
				+ "requirePrefix: appns")
				.and(app).hasDefaultEnvironmentProperties("SOME.TOKEN", "token replacement")
				.and(app).hasEnvironmentProperties("prod", "SOME.TOKEN", "prod replacement")
				.and(aspect).hasBeenCreated()
				.and(aspect).containsFileWithContents("src/App.js", "@SOME.TOKEN@")
				.and(aspect).indexPageRequires("appns/App")
				.and(brjs).hasVersion("dev");
		when(brjs).runThreadedCommand("serve", "--environment", "prod");
		then(appServer).requestForUrlContains("/app1/v/dev/js/dev/combined/bundle.js", "prod replacement");
	}

	@Test //log at info since when running 'serve' the JNDI token filter will replace more tokens
	public void warningIsLoggedAtInfoLevelIfTokenCannotBeReplacedAndWebInfExists() throws Exception
	{
		given(app).hasBeenCreated()
				.and(app).containsFolder("WEB-INF")
				.and(app).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
				+ "locales: en\n"
				+ "requirePrefix: appns")
				.and(aspect).hasBeenCreated()
				.and(aspect).containsFileWithContents("src/App.js", "@SOME.TOKEN@")
				.and(aspect).indexPageRequires("appns/App")
				.and(brjs).hasVersion("dev")
				.and(logging).enabled();
		when(brjs).runThreadedCommand("serve", "-e", "prod");
		then(appServer).requestForUrlContains("/app1/v/dev/js/dev/combined/bundle.js", "An error occurred when the token finder 'JndiTokenFinder' attempted to locate a replacement for the the token 'SOME.TOKEN'.") // exception thrown when the JNDI filter tries to replace the same token
			.and(logging).unorderedInfoMessageReceived(LoggingMissingTokenHandler.NO_TOKEN_REPLACEMENT_MESSAGE, "SOME.TOKEN", "prod" );
	}
	
	@Test
	public void exceptionIsThrownIfTokenCannotBeReplacedForAppWhereWebInfIsNotPresent() throws Exception
	{
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
			+ "locales: en\n"
			+ "requirePrefix: appns")
			.and(aspect).hasBeenCreated()
			.and(aspect).containsFileWithContents("src/App.js", "@SOME.TOKEN@")
			.and(aspect).indexPageHasContent("<@js.bundle@/>\n"+"require('appns/App');")
			.and(brjs).hasVersion("123");
		when(brjs).runThreadedCommand("serve")
			.and(appServer).requestIsMadeFor("/app1/v/dev/js/dev/combined/bundle.js", response);
		then(response).containsText("The token finder 'PropertyFileTokenFinder' could not find a replacement for the token 'SOME.TOKEN'.");
	}
	
	@Test
	public void tokensCanBeReplacedFromPropertyFilesInAspectIndexHtml() throws Exception
	{
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
			+ "locales: en\n"	
			+ "requirePrefix: appns")
			.and(app).hasDefaultEnvironmentProperties("SOME.TOKEN", "token replacement")
			.and(aspect).hasBeenCreated()
			.and(aspect).indexPageHasContent("@SOME.TOKEN@");
		when(brjs).runThreadedCommand("serve");
		then(appServer).requestForUrlReturns("/app1/", "token replacement");
	}
	
	@Test
	public void tokensCanBeReplacedFromPropertyFilesInAspectIndexJsp() throws Exception
	{
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
			+ "locales: en\n"	
			+ "requirePrefix: appns")
			.and(app).hasDefaultEnvironmentProperties("SOME.TOKEN", "token replacement")
			.and(aspect).hasBeenCreated()
			.and(aspect).containsFileWithContents("index.jsp","@SOME.TOKEN@ <%= 1 + 2 %>");
		when(brjs).runThreadedCommand("serve");
		then(appServer).requestForUrlReturns("/app1/", "token replacement 3");
	}
	
	@Test
	public void weCanUseUTF8() throws Exception {
		given(brjs.bladerunnerConf()).defaultFileCharacterEncodingIs("UTF-8")
			.and().activeEncodingIs("UTF-8")
			.and(app).hasBeenCreated()
    		.and(app).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
    		+ "locales: en\n"
    		+ "requirePrefix: appns")
    		.and(app).hasDefaultEnvironmentProperties("SOME.TOKEN", "token replacement")
    		.and(aspect).hasBeenCreated()
    		.and(aspect).containsFileWithContents("src/App.js", "@SOME.TOKEN@ // $£€")
    		.and(aspect).indexPageRequires("appns/App")
    		.and(brjs).hasVersion("dev");
		when(brjs).runThreadedCommand("serve");
    	then(appServer).requestForUrlContains("/app1/v/dev/js/dev/combined/bundle.js", "token replacement // $£€");
	}
	
	@Test
	public void weCanUseLatin1() throws Exception {
		given(brjs.bladerunnerConf()).defaultFileCharacterEncodingIs("ISO-8859-1")
    		.and().activeEncodingIs("ISO-8859-1")
    		.and(app).hasBeenCreated()
    		.and(app).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
    		+ "locales: en\n"
    		+ "requirePrefix: appns")
    		.and(app).hasDefaultEnvironmentProperties("SOME.TOKEN", "token replacement")
    		.and(aspect).hasBeenCreated()
    		.and(aspect).containsFileWithContents("src/App.js", "@SOME.TOKEN@ // $£")
    		.and(aspect).indexPageRequires("appns/App")
    		.and(brjs).hasVersion("dev");
    	when(brjs).runThreadedCommand("serve");
    	then(appServer).requestForUrlContains("/app1/v/dev/js/dev/combined/bundle.js", "token replacement // $£");
	}
	
	@Test
	public void tokenReplacementValuesCanBeChangedWhileTheServerIsRunning() throws Exception
	{
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
			+ "locales: en\n"	
			+ "requirePrefix: appns")
			.and(app).hasDefaultEnvironmentProperties("SOME.TOKEN", "token replacement")
			.and(aspect).hasBeenCreated()
			.and(aspect).indexPageHasContent("@SOME.TOKEN@");
		when(brjs).runThreadedCommand("serve")
			.and(appServer).requestIsMadeFor("/app1/", response)
			.and(app).hasDefaultEnvironmentProperties("SOME.TOKEN", "new token replacement value");
		then(response).textEquals("token replacement") /* the first response */
			.and(appServer).requestForUrlReturns("/app1/", "new token replacement value"); /* response after the new token value */
	}

}
