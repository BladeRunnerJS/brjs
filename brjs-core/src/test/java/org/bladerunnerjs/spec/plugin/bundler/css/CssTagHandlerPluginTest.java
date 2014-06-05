package org.bladerunnerjs.spec.plugin.bundler.css;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class CssTagHandlerPluginTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();
	private Aspect loginAspect;
	private Bladeset bladeset;
	private Blade blade;
	private Workbench workbench;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlers().and(brjs).automaticallyFindsMinifiers().and(brjs).hasBeenCreated();
		app = brjs.app("app1");
		aspect = app.aspect("default");
		blade = app.bladeset("bs").blade("b1");
		loginAspect = app.aspect("login");
		bladeset = app.bladeset("bs");
		blade = bladeset.blade("b1");
		workbench = blade.workbench();
	}
	
	@Test
	public void languageBasedTokenTagIsConvertedToSeriesOfStylesheetIncludes() throws Exception {
		given(aspect).indexPageHasContent("<@css.bundle@/>");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(response).containsOrderedTextFragments(
			"<link rel='stylesheet' href='../v/dev/css/common/bundle.css'/>",
			"<link rel='stylesheet' href='../v/dev/css/common_en/bundle.css'/>");
	}
	
	@Test
	public void localeBasedTokenTagIsConvertedToSeriesOfStylesheetIncludes() throws Exception {
		given(aspect).indexPageHasContent("<@css.bundle@/>");
		when(aspect).indexPageLoadedInDev(response, "en_GB");
		then(response).containsOrderedTextFragments(
			"<link rel='stylesheet' href='../v/dev/css/common/bundle.css'/>",
			"<link rel='stylesheet' href='../v/dev/css/common_en/bundle.css'/>",
			"<link rel='stylesheet' href='../v/dev/css/common_en_GB/bundle.css'/>");
	}
	
	@Test
	public void onlyThemesForTheGivenAspectAreIncludedInGeneratedTags() throws Exception {
		given(loginAspect).containsFile("themes/login/wibble.css")
			.and(loginAspect).containsFile("themes/aspect1/wibble.css")
			.and(loginAspect).containsFile("themes/aspect2/wibble.css")
			.and(loginAspect).indexPageHasContent("<@css.bundle@/>\n" + "appns.bs.b1.Class1()")
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(blade).containsFile("themes/aspect/wibble.css");
		when(loginAspect).indexPageLoadedInDev(response, "en_GB");
		then(response).containsOrderedTextFragments(
			"<link rel='stylesheet' href='../v/dev/css/common/bundle.css'/>",
			"<link rel='stylesheet' href='../v/dev/css/common_en/bundle.css'/>",
			"<link rel='stylesheet' href='../v/dev/css/common_en_GB/bundle.css'/>")
			.and(response).doesNotContainText("href='css/aspect");
	}
	
	@Test
	public void themesReferencedInCssTagsAreIncludedInGeneratedTags() throws Exception {
		given(aspect).containsFile("themes/standard/wibble.css")
			.and(aspect).indexPageHasContent("<@css.bundle theme=\"standard\"@/>");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(response).containsOrderedTextFragments(
			"<link rel='stylesheet' href='../v/dev/css/common/bundle.css'/>",
			"<link rel='stylesheet' href='../v/dev/css/common_en/bundle.css'/>",
			"<link rel='stylesheet' title='standard' href='../v/dev/css/standard/bundle.css'/>");
	}
	
	@Test
	public void bladeThemesAreUsedInAWorkbenchEvenIfTheAspectDoesNotHaveThatTheme() throws Exception {
		given(aspect).containsFile("themes/standard/wibble.css")
			.and(workbench).indexPageHasContent("<@css.bundle theme=\"standard\"@/>\n");
		when(workbench).pageLoaded(response, "en");
		then(response).containsOrderedTextFragments(
				"<link rel='stylesheet' href='../v/dev/css/common/bundle.css'/>",
				"<link rel='stylesheet' href='../v/dev/css/common_en/bundle.css'/>",
				"<link rel='stylesheet' title='standard' href='../v/dev/css/standard/bundle.css'/>");

	}
	
	@Test
	public void themesDefinedMultipleTimesOnlyHaveASingleReuqest() throws Exception {
		given(aspect).containsFile("themes/standard/wibble.css")
			.and(bladeset).containsFile("themes/standard/wibble.css")
			.and(bladeset).hasClass("appns/bs/Class")
			.and(blade).containsFile("themes/standard/wibble.css")
			.and(blade).hasClass("appns/bs/b1/Class")
			.and(aspect).indexPageHasContent("<@css.bundle theme=\"standard\"@/> appns.bs.Class  appns.bs.b1.Class ");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(response).containsTextOnce("<link rel='stylesheet' href='../v/dev/css/common/bundle.css'/>")
			.and(response).containsTextOnce("<link rel='stylesheet' href='../v/dev/css/common_en/bundle.css'/>")
			.and(response).containsTextOnce("<link rel='stylesheet' title='standard' href='../v/dev/css/standard/bundle.css'/>");
			
			
	}

}
