package org.bladerunnerjs.plugin.checki18n;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.AppConf;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class CheckI18nCommandTest extends SpecTest
{
	private App app;
	private AppConf appConf;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private TestPack bladeUTs, bladeATs;

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
			bladeUTs = blade.testType("unit").testTech("TEST_TECH");
			bladeATs = blade.testType("acceptance").testTech("TEST_TECH");
	}	
	
	@Test
	public void testCheckI18nCommandExists() throws Exception
	{
		given(app).hasBeenCreated();
		when(brjs).runCommand("check-i18n", "app1");
		then(exceptions).verifyNoOutstandingExceptions();		
	}

	@Test
	public void exceptionIsThrownIfThereAreTooFewArguments() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("check-i18n");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'app-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownWhenAppDoesNotExist() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("check-i18n", "unknownApp");
		then(exceptions).verifyException(CommandArgumentsException.class, unquoted("The app 'unknownApp' does not exist"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void testAppWithNoTokensReportsNoMissingTranslations() throws Exception
	{
		given(app).hasBeenCreated();
		when(brjs).runCommand("check-i18n", "app1");
		then(logging).containsConsoleText("For the locale en, app1 has no missing translations");		
	}
	
	@Test
	public void testAppWithAMissingTokenShowsCorrectMessage() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).indexPageHasContent("<p>@{appns.bs.b1.missingtoken}</p>")
			.and(aspect).containsResourceFileWithContents("en.properties", "appns.bs.b1.dummytoken=dummy value");	
		when(brjs).runCommand("check-i18n", "app1");
		then(logging).containsConsoleText("\n" + "For the locale en, app1 has no translations defined for the following tokens:" + "\n",
										"appns.bs.b1.missingtoken");		
	}
	
	@Test
	public void testAppWithADuplciateMissingTokenShowsCorrectMessage() throws Exception
	{
		given(app).hasBeenCreated()
		.and(aspect).indexPageHasContent("<p>@{appns.bs.b1.missingtoken}</p>")
		.and(blade).containsResourceFileWithContents("file.xml", "<some-xml value=@{appns.bs.b1.missingtoken}></some-xml>")
		.and(blade).containsResourceFileWithContents("en.properties", "");	
		when(brjs).runCommand("check-i18n", "app1");
		then(logging).containsConsoleText("\n" + "For the locale en, app1 has no translations defined for the following tokens:" + "\n",
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
	
	@Test
	public void testWhenTokenUsedInXmlIsMissingItAppearsInList() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).indexPageHasContent("<p>@{appns.bs.b1.goodtoken}</p>")
			.and(blade).containsResourceFileWithContents("file.xml", "<some-xml value=@{appns.bs.b1.missingtoken}></some-xml>")
			.and(blade).containsResourceFileWithContents("en.properties", "appns.bs.b1.goodtoken=some value");	
		when(brjs).runCommand("check-i18n", "app1");
		then(logging).containsConsoleText("\n" + "For the locale en, app1 has no translations defined for the following tokens:" + "\n",
										"appns.bs.b1.missingtoken");		
	}
	
	@Test
	public void testDisplaysCorrectMessageWhenTokenUsedInXmlIsPresent() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).indexPageHasContent("appns.bs.b1.bladestuff")
			.and(blade).containsResourceFileWithContents("file.xml", "<some-xml value=@{appns.bs.b1.agoodtoken}></some-xml>")
			.and(blade).containsResourceFileWithContents("en.properties", "appns.bs.b1.agoodtoken=some value");	
		when(brjs).runCommand("check-i18n", "app1");
		then(logging).containsConsoleText("For the locale en, app1 has no missing translations");		
	}
	
	@Test
	public void testI18nTokensFoundInTestFilesAreChecked() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).indexPageHasContent("appns.bs.b1.bladestuff")
			.and(bladeUTs).testFileHasContent("test.js", "i18n('appns.bs.b1.missing')");
		when(brjs).runCommand("check-i18n", "app1");
		then(logging).containsConsoleText("\n" + "For the locale en, app1 has no translations defined for the following tokens:" + "\n",
								"appns.bs.b1.missing");		
	}
	
	@Test
	public void testDisplaysCorrectMessageWhenBladeSrcFileUsesMissingToken() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasCommonJsPackageStyle()
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classFileHasContent("appns.Class1", "i18n('appns.bs.b1.missing')");
		when(brjs).runCommand("check-i18n", "app1");
		then(logging).containsConsoleText("\n" + "For the locale en, app1 has no translations defined for the following tokens:" + "\n",
				"appns.bs.b1.missing");				
	}
	
	@Test
	public void testThatMissingTokensThatTakeAParamaterAreShownInTheList() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasCommonJsPackageStyle()
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classFileHasContent("appns.Class1", "i18n('appns.bs.b1.missing.', param)");
		when(brjs).runCommand("check-i18n", "app1");
		then(logging).containsConsoleText("\n" + "For the locale en, app1 has no translations defined for the following tokens:" + "\n",
				"appns.bs.b1.missing");				
	}
	
	@Test
	public void testATokenThatHasAMissingPartCanBePartiallyMatched() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasCommonJsPackageStyle()
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classFileHasContent("appns.Class1", "i18n('appns.bs.b1.unknownending.' + value)")
			.and(aspect).containsResourceFileWithContents("en.properties", "appns.bs.b1.unknownending.potentialend=some value");
		when(brjs).runCommand("check-i18n", "app1");
		then(logging).containsConsoleText("For the locale en, app1 has no missing translations");			
	}
	
	@Test
	public void testATokenThatHasAMissingPartAndCannotBePartiallyMatchedIsLoggedAsMissingWithAsterisk() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasCommonJsPackageStyle()
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classFileHasContent("appns.Class1", "i18n('appns.bs.b1.unknownending.' + value)")
			.and(aspect).containsResourceFileWithContents("en.properties", "appns.bs.b1.validtoken=some value");
		when(brjs).runCommand("check-i18n", "app1");
		then(logging).containsConsoleText("\n" + "For the locale en, app1 has no translations defined for the following tokens:" + "\n",
				"appns.bs.b1.unknownending.* a token beginning with this prefix could not be found");
	}
	
	@Test
	public void testATokenThatTakesAParamameterCannotBePartiallyMatched() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasCommonJsPackageStyle()
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classFileHasContent("appns.Class1", "i18n('appns.bs.b1.missing', param)")
			.and(aspect).containsResourceFileWithContents("en.properties", "appns.bs.b1.missing.othertoken=some value");
		when(brjs).runCommand("check-i18n", "app1");
		then(logging).containsConsoleText("\n" + "For the locale en, app1 has no translations defined for the following tokens:" + "\n",
				"appns.bs.b1.missing");				
	}

	@Test
	public void testAsteriskIsAddedForMissingTokensThatAreConstructedInThreeParts() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasCommonJsPackageStyle()
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classFileHasContent("appns.Class1", "i18n('appns.bs.b1.missing.' + value + '.token')");
		when(brjs).runCommand("check-i18n", "app1");
		then(logging).containsConsoleText("\n" + "For the locale en, app1 has no translations defined for the following tokens:" + "\n",
				"appns.bs.b1.missing.* a token beginning with this prefix could not be found");
	}
	
	@Test
	public void testAsteriskIsAddedForMissingTokensThatAreConcatenated() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasCommonJsPackageStyle()
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classFileHasContent("appns.Class1", "i18n('appns.bs.b1.missing.' + value)");
		when(brjs).runCommand("check-i18n", "app1");
		then(logging).containsConsoleText("\n" + "For the locale en, app1 has no translations defined for the following tokens:" + "\n",
						"appns.bs.b1.missing.* a token beginning with this prefix could not be found");
	}
}
