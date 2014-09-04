package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.plugin.plugins.commands.standard.J2eeifyCommandPlugin;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class J2eeifyCommandPluginTest extends SpecTest {
	App app;
	App badApp;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommandPlugins(new J2eeifyCommandPlugin())
			.and(brjs).hasBeenCreated()
			.and(brjs).usesProductionTemplates();
			app = brjs.app("app");
			badApp = brjs.app("app#$@/");
			brjs.appJars().create();;
			
		given(app).hasBeenCreated();
	}
	
	@After
	public void stopAppServer() throws Exception {
		given(brjs.applicationServer(appServerPort)).stopped()
			.and(brjs.applicationServer(appServerPort)).requestTimesOutFor("/");
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooFewArguments() throws Exception {
		when(brjs).runCommand("j2eeify");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'app-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception {
		when(brjs).runCommand("j2eeify", "a", "b", "c");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: b"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAppDoesntExist() throws Exception {
		when(brjs).runCommand("j2eeify", "idontexist");
		then(exceptions).verifyFormattedException(CommandArgumentsException.class, J2eeifyCommandPlugin.Messages.APP_DOES_NOT_EXIST_EXCEPTION, "idontexist");
	}
	
	@Test
	public void templateFilesAreCopied() throws Exception {
		when(brjs).runCommand("j2eeify", "app");
		then(app).hasDir("WEB-INF")
			.and(app).hasFile("WEB-INF/web.xml")
			.and(app).hasFile("WEB-INF/classes/log4j.properties")
			.and(app).hasFile("WEB-INF/lib/slf4j-log4j12-1.6.6.jar");
	}
	
	@Test
	public void webInfIsPopulatedFromApplicationServerUtils() throws Exception {
		when(brjs).runCommand("j2eeify", "app");
		then(app).fileContentsContains("WEB-INF/web.xml", "<servlet-class>org.bladerunnerjs.appserver.BRJSDevServlet</servlet-class>")
			.and(app).fileContentsDoesNotContain("WEB-INF/web.xml", "This web.xml should be empty");
	}
	
	@Test
	public void messageIsPrintedOnSucces() throws Exception {
		when(brjs).runCommand("j2eeify", "app");
		then(logging).containsFormattedConsoleMessage(J2eeifyCommandPlugin.Messages.SUCCESSFULLY_J2EEIFIED_APP_MESSAGE, "app", "apps/app/WEB-INF");
	}
	
	@Test
	public void applicationServerStillStartsAfterWebInfIsCopiedIn() throws Exception {
		given(app.defaultAspect()).indexPageHasContent("APP INDEX PAGE")
			.and(brjs.applicationServer(appServerPort)).started()
			.and(brjs.applicationServer(appServerPort)).requestForUrlContains("/app/en/", "APP INDEX PAGE")
			.and(brjs.applicationServer(appServerPort)).stopped()
			.and(brjs.applicationServer(appServerPort)).requestTimesOutFor("/");
		when(brjs).runCommand("j2eeify", "app")
			.and(brjs.applicationServer(appServerPort)).started();
		then(brjs.applicationServer(appServerPort)).requestForUrlContains("/app/en/", "APP INDEX PAGE");
	}
	
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated();
		when(brjs).runCommand("j2eeify", "app");
		then(exceptions).verifyNoOutstandingExceptions();
	}
}
