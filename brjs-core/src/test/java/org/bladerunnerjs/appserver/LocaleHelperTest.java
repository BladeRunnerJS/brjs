package org.bladerunnerjs.appserver;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.*;

import org.bladerunnerjs.appserver.LocaleHelper;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.bladerunnerjs.utility.FileUtility;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Joiner;

public class LocaleHelperTest
{
	private HttpServletRequest request;
	private Vector<Locale> preferedLocales;
	private App app;
	private AppConf appConf;
	
	@Before
	public void setup() throws Exception
	{
		request = mock(HttpServletRequest.class);
		preferedLocales = new Vector<Locale>();
		
		File tempDir = createTestSdkDirectory();
		BRJS brjs = BRJSTestFactory.createBRJS(tempDir);
		app = brjs.app("app");
		appConf = app.appConf();
	}
	
	@Test
	public void getLanguageFromLocale()
	{
		String language = LocaleHelper.getLanguageFromLocale("en_GB");
		assertEquals("en", language);
	}
	
	@Test
	public void browserGetsTheDefaultLocaleIfNoneOfTheirPreferencesAreAvailable() throws Exception
	{
		givenServerSupportsLocales("en", "es");
			andBrowserPrefersLocales("de_DE", "de");
		whenRequestIsMade();
		thenLocaleProvidedIs("en");
	}
	
	@Test
	public void browserAlsoGetsTheDefaultLocaleIfThisMatchesTheirPreference() throws Exception
	{
		givenServerSupportsLocales("en", "es");
			andBrowserPrefersLocales("en_GB", "en");
		whenRequestIsMade();
		thenLocaleProvidedIs("en");
	}
	
	@Test
	public void browserGetsNonDefaultLocaleIfThisMatchesTheirPreference() throws Exception
	{
		givenServerSupportsLocales("en", "es");
			andBrowserPrefersLocales("es_ES", "es");
		whenRequestIsMade();
		thenLocaleProvidedIs("es");
	}
	
	@Test
	public void browserGetsCountrySpecificLocale() throws Exception
	{
		givenServerSupportsLocales("en_GB", "en");
			andBrowserPrefersLocales("en_GB", "en");
		whenRequestIsMade();
		thenLocaleProvidedIs("en_GB");
	}
	
	@Test
	public void browserGetsCountrySpecificLocaleEvenWhenThatAppearsSecondInTheListOfSupportedLocales() throws Exception
	{
		givenServerSupportsLocales("en", "en_GB");
			andBrowserPrefersLocales("en_GB", "en");
		whenRequestIsMade();
		thenLocaleProvidedIs("en_GB");
	}
	
	@Test
	public void browserGetsGeneralLocaleIfTheirLanguageSpecificVariationMayNotBeAvailable() throws Exception
	{
		givenServerSupportsLocales("en", "en_GB");
			andBrowserPrefersLocales("en_US", "en");
		whenRequestIsMade();
		thenLocaleProvidedIs("en");
	}
	
	@Test
	public void browserGetsFirstLanguageInTheirListEvenIfTheySupportTheDefault() throws Exception
	{
		givenServerSupportsLocales("en", "es");
			andBrowserPrefersLocales("es_ES", "es", "en");
		whenRequestIsMade();
		thenLocaleProvidedIs("es");
	}
	
	@Test
	public void localeSpecifiedInCookieOverridesBrowserLocales() throws Exception
	{
		givenServerSupportsLocales("en", "de", "es");
			andBrowserPrefersLocales("en_GB", "en");
			andBrowserHasCookies(
				new Cookie("some-cookie", "a-value"),
				new Cookie("another-cookie", "a-value"),
				new Cookie("BRJS.LOCALE", "de"));
		whenRequestIsMade();
		thenLocaleProvidedIs("de");
	}
	
	@Test
	public void browserFallsBackToNormalSelectionBehaviourIfTHeCookieSpecifiesAnUnsupportedLocale() throws Exception
	{
		givenServerSupportsLocales("en", "de", "es");
			andBrowserPrefersLocales("es_ES", "es");
			andBrowserHasCookies(
				new Cookie("CAPLIN.LOCALE", "zh_CN"));
		whenRequestIsMade();
		thenLocaleProvidedIs("es");
	}
	
	private void givenServerSupportsLocales(String... locales) throws ConfigException
	{
		appConf.setLocales( Joiner.on(",").join(locales) );
	}
	
	private void andBrowserPrefersLocales(String... locales)
	{
		for(String locale : locales) {
			String[] localeSplit = locale.split("_");
			
			if (localeSplit.length == 1)
			{
				preferedLocales.add(new Locale(localeSplit[0]));
			}
			else
			{
				preferedLocales.add(new Locale(localeSplit[0],localeSplit[1]));
			}
		}
	}
	
	private void andBrowserHasCookies(Cookie... cookies) {
		when(request.getCookies()).thenReturn( cookies );
	}
	
	private void whenRequestIsMade() {
		when(request.getLocales()).thenReturn( preferedLocales.elements() );
	}
	
	private void thenLocaleProvidedIs(String expectedLocale) throws Exception {
		assertEquals(expectedLocale, LocaleHelper.getLocaleFromRequest(app, request));
	}
	
	
	private File createTestSdkDirectory() {
		File sdkDir;
		
		try {
			sdkDir = FileUtility.createTemporaryDirectory("test");
			new File(sdkDir, "sdk").mkdirs();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return sdkDir;
	}
}
