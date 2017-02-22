package org.bladerunnerjs.plugin.checki18n;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class CheckI18nCommandTest extends SpecTest
{
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private TestPack bladeUTs;

	@Before
	public void initTestObjects() throws Exception
	{		
		given(brjs).automaticallyFindsAllPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			bladeUTs = blade.testType("unit").testTech("TEST_TECH");
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
		given(app).hasBeenCreated()
			.and(aspect).indexPageHasContent("<p>no tokens</p>");
		when(brjs).runCommand("check-i18n", "app1");
		then(logging).containsConsoleText("For the locale en, app1 has no missing translations");		
	}
	
	@Test
	public void testAppWithAMissingTokenShowsCorrectMessage() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).indexPageHasContent("<p>@{appns.ns.missingtoken}</p>")
			.and(aspect).containsResourceFileWithContents("en.properties", "appns.ns.dummytoken=dummy value");	
		when(brjs).runCommand("check-i18n", "app1");
		then(logging).containsConsoleText("checking default aspect\n\n" + "For the locale en, app1 has no translations defined for the following tokens:" + "\n",
										"appns.ns.missingtoken");		
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
	public void testAdditionalMessageIsAddedForMissingTokensThatAreConstructedInThreeParts() throws Exception
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
	public void testAddiotionalMessageIsAddedForMissingTokensThatAreConcatenated() throws Exception
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
	
	@Test
	public void testAppWithMultipleLocalesShowsMissingTokensForAllLocales() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "requirePrefix: app1\nlocales: en_GB, de_DE")
			.and(aspect).indexPageHasContent("<p>@{appns.missingtoken}</p>")
			.and(aspect).containsResourceFileWithContents("en_GB.properties", "appns.property=property value")
			.and(aspect).containsResourceFileWithContents("de_DE.properties", "appns.property=a different value");	
		when(brjs).runCommand("check-i18n", "app1");
		then(logging).containsConsoleText("\n" + "For the locale de, app1 has no translations defined for the following tokens:" + "\n",
										"appns.missingtoken",
										"\n" + "For the locale en, app1 has no translations defined for the following tokens:" + "\n",
										"appns.missingtoken");
	}
	
	@Test
	public void testAppWithMultipleLocalesOnlyMissingLocalesForSpecifiedLocaleAreLogged() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "requirePrefix: app1\nlocales: en_GB, de_DE")
			.and(aspect).indexPageHasContent("<p>@{appns.missingtoken}</p>")
			.and(aspect).containsResourceFileWithContents("en_GB.properties", "appns.property=property value")
			.and(aspect).containsResourceFileWithContents("de_DE.properties", "appns.property=a different value");	
		when(brjs).runCommand("check-i18n", "app1", "de");
		then(logging).containsConsoleText("\n" + "For the locale de, app1 has no translations defined for the following tokens:" + "\n",
										"appns.missingtoken");		
	}
	
	@Test
	public void testCommandGeneratesCSVFileWithCorrectContents() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "requirePrefix: app1\nlocales: en_GB, de_DE")
			.and(aspect).indexPageHasContent("<p>@{appns.missingtoken}</p>")
			.and(aspect).containsResourceFileWithContents("en_GB.properties", "appns.property=english words")
			.and(aspect).containsResourceFileWithContents("de_DE.properties", "appns.property=german words");	
		when(brjs).runCommand("check-i18n", "app1");
		then(brjs).hasDir("generated/CheckI18nCommand")
			.and(brjs).hasFile("generated/CheckI18nCommand/app1-tokens.csv")
			.and(brjs).fileHasContents("generated/CheckI18nCommand/app1-tokens.csv", "** the 'IsUsed' column only relates to tokens identified"
																+ " in their entirety and will not include tokens which are concatenated e.g. i18n('a.token.' + value);",
																"Token,de,en,IsUsed",
																"appns.missingtoken,,,true",
																"appns.property,german words,english words,false\n"
																);
	}
	
}
