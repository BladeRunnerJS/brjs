package org.bladerunnerjs.spec.plugin.bundler.i18n;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class I18nTagHandlerPluginTest extends SpecTest
{

	private App app;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
	}

	@Test
	public void i18nTokenPluginContainsJsFunction() throws Exception {
		given(aspect).indexPageHasContent("<@i18n.bundle@/>");
    	when(aspect).indexPageLoadedInDev(response, "en_GB");
    	then(response).containsOrderedTextFragments(
    			"getCookieLocale() {",
    			"var localeCookieName = \"brjsLocale=\";",
    			"getLocale() {",
    			"document.write('<scr'+'ipt src=\"i18n/' + getLocale() + '.js\"></script>');" );
	}
	
	@Test
	public void localeCookieNameIsConfigurable() throws Exception {
		given(aspect).indexPageHasContent("<@i18n.bundle cookieName='appLocale' @/>");
    	when(aspect).indexPageLoadedInDev(response, "en_GB");
    	then(response).containsOrderedTextFragments(
    			"getCookieLocale() {",
    			"var localeCookieName = \"appLocale=\";" );
	}
	
	@Test
	public void getLocaleMethodHasCorrectSupportedLocales() throws Exception {
		given(app).hasSupportedLocales("en, en_GB, de")
			.and(aspect).indexPageHasContent("<@i18n.bundle@/>");
		when(aspect).indexPageLoadedInDev(response, "en_GB");
		then(response).containsOrderedTextFragments(
				"getLocale() {",
				"var supportedLocales = [\"en\",\"en_GB\",\"de\"]; var defaultLocale = \"en\";  var browserLocale = browserLocale;" );
	}
	
	@Test
	public void getLocaleMethodHasCorrectDefaultLocale() throws Exception {
		given(app).hasSupportedLocales("en, en_GB, de")
			.and(aspect).indexPageHasContent("<@i18n.bundle@/>");
		when(aspect).indexPageLoadedInDev(response, "en_GB");
		then(response).containsOrderedTextFragments(
				"getLocale() {",
				"var defaultLocale = \"en\";" );
	}
	
}
