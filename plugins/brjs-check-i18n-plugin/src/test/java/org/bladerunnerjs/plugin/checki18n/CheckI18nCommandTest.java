package org.bladerunnerjs.plugin.checki18n;


import static org.mockito.Mockito.*;


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
	private StringBuffer response = new StringBuffer();
	private Bladeset bladeset;
	private Blade blade;

	@Before
	public void initTestObjects() throws Exception
	{
		
		given(brjs).automaticallyFindsAllPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			app = brjs.app("app1");
			appConf = app.appConf();
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

}
