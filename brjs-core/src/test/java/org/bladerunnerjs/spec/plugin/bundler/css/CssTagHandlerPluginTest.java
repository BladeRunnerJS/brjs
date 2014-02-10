package org.bladerunnerjs.spec.plugin.bundler.css;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class CssTagHandlerPluginTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlers().and(brjs).automaticallyFindsMinifiers().and(brjs).hasBeenCreated();
		app = brjs.app("app1");
		aspect = app.aspect("default");
	}
	
	@Test
	public void tokenPluginIsConvertedToSeriesOfStylesheetIncludes() throws Exception {
		given(aspect).indexPageHasContent("<@css.bundle@/>");
		when(aspect).indexPageLoadedInDev(response, "en_GB");
		then(response).containsOrderedTextFragments(
			"<link rel='stylesheet' href='css/common/bundle.css'/>",
			// TODO: understand if the tag handler is wrong not to produce this request, or the content-plugin is wrong not to serve language stylesheets when it receives a request for a full locale
//			"<link rel='stylesheet' href='css/common_en/bundle.css'/>",
			"<link rel='stylesheet' href='css/common_en_GB/bundle.css'/>");
	}
}
