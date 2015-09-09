package org.bladerunnerjs.spec.plugin.bundler.i18n;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.spec.engine.SpecTest;
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
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
	}

	@Test
	public void i18nTokenPluginContainsJsRequest() throws Exception {
		given(aspect).indexPageHasContent("<@i18n.bundle@/>");
		when(aspect).indexPageLoadedInDev(response, "en_GB");
		then(response).containsText("<script type=\"text/javascript\" src=\"v/dev/i18n/en_GB.js\"></script>");
	}

	@Test
	public void i18nTokenPluginContainsJsRequestForBothCurrentAndDefaultLocalesInTheCorrectOrder() throws Exception {
		given(aspect).indexPageHasContent("<@i18n.bundle@/>")
			.and(app.appConf()).supportsLocales("en_GB","de_DE");
		when(aspect).indexPageLoadedInDev(response, "de_DE");
		then(response).containsOrderedTextFragments(
				"<script type=\"text/javascript\" src=\"v/dev/i18n/en_GB.js\"></script>",
				"<script type=\"text/javascript\" src=\"v/dev/i18n/de_DE.js\"></script>");
	}
	
}
