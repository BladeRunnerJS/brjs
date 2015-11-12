package org.bladerunnerjs.plugin.checki18n;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.AppConf;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class CheckI18nCommandTest extends SpecTest
{
	private App app;
	private AppConf appConf;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;

	@Before
	public void initTestObjects() throws Exception
	{		
		given(brjs).automaticallyFindsAllPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			appConf = app.appConf();
			aspect = app.aspect("default");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
	}	
	
	@Test
	public void testCheckI18nCommandExists() throws Exception
	{
		when(brjs).runCommand("check-i18n", "app1");
		then(exceptions).verifyNoOutstandingExceptions();		
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooFewArguments() throws Exception {
		when(brjs).runCommand("check-i18n");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'app-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void testAppWithNoTokensReportsNoMissingTranslations() throws Exception
	{
		when(brjs).runCommand("check-i18n", "app1");
		then(logging).containsConsoleText("For the locale en, app1 has no missing translations");		
	}

	@Test
	public void testAppWithAMissingToken() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).indexPageHasContent("<p>@{appns.bs.b1.missingtoken}</p>")
			.and(aspect).containsResourceFileWithContents("en.properties", "appns.bs.b1.dummytoken=dummy value");	
		when(brjs).runCommand("check-i18n", "app1");
		then(logging).containsConsoleText("For the locale en, app1 has no translations defined for the following token:",
										"appns.bs.b1.missingtoken");		
	}
	
	@Test
	public void testWhenHasAppHasAllTranslatedTokensLoggerShowsNoMissingTokens() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).indexPageHasContent("<p>@{appns.bs.b1.atoken}</p>")
			.and(aspect).containsResourceFileWithContents("en.properties", "appns.bs.b1.atoken=some value");	
		when(brjs).runCommand("check-i18n", "app1");
		then(logging).containsConsoleText("For the locale en, app1 has no missing translations");		
	}	
	
}
