package org.bladerunnerjs.spec.plugin.bundler.css;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.plugin.plugins.bundlers.css.CssTagHandlerPlugin;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class CssTagHandlerPluginTest extends SpecTest {
	private App app;
	private AppConf appConf;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();
	private Aspect loginAspect;
	private File commonTheme;
	private File standardTheme;
	private Bladeset bladeset;
	private Blade blade;
	private Workbench workbench;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlers().and(brjs).automaticallyFindsMinifiers().and(brjs).hasBeenCreated();
		app = brjs.app("app1");
		appConf = app.appConf();
		aspect = app.aspect("default");
		commonTheme = aspect.file("themes/common");
		standardTheme = aspect.file("themes/standard");
		blade = app.bladeset("bs").blade("b1");
		loginAspect = app.aspect("login");
		bladeset = app.bladeset("bs");
		blade = bladeset.blade("b1");
		workbench = blade.workbench();
	}
	
	@Test
	public void languageBasedTokenTagIsConvertedToSeriesOfStylesheetIncludes() throws Exception {
		given(aspect).indexPageHasContent("<@css.bundle@/>")
			.and(aspect).containsResourceFiles("style.css", "style_en.css", "style_en_GB.css")
			.and(appConf).supportsLocales("en", "en_GB");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(response).containsOrderedTextFragments(
			"<link rel=\"stylesheet\" href=\"../v/dev/css/common/bundle.css\"/>",
			"<link rel=\"stylesheet\" href=\"../v/dev/css/common_en/bundle.css\"/>");
	}
	
	@Test
	public void localeBasedTokenTagIsConvertedToSeriesOfStylesheetIncludes() throws Exception {
		given(aspect).indexPageHasContent("<@css.bundle@/>")
			.and(aspect).containsResourceFiles("style.css", "style_en.css", "style_en_GB.css")
			.and(appConf).supportsLocales("en", "en_GB");
		when(aspect).indexPageLoadedInDev(response, "en_GB");
		then(response).containsOrderedTextFragments(
			"<link rel=\"stylesheet\" href=\"../v/dev/css/common/bundle.css\"/>",
			"<link rel=\"stylesheet\" href=\"../v/dev/css/common_en/bundle.css\"/>",
			"<link rel=\"stylesheet\" href=\"../v/dev/css/common_en_GB/bundle.css\"/>");
	}
	
	@Test
	public void themesForUsedBladesCanBeLoadedEvenIfTheyArenThemesForTheAspect() throws Exception {
		given(aspect).containsFile("themes/aspect1/style.css")
			.and(aspect).containsFile("themes/aspect2/style.css")
			.and(loginAspect).indexPageHasContent("<@css.bundle theme=\"blade-theme\"@/>\n" + "appns.bs.b1.Class1()")
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(blade).containsFile("themes/blade-theme/style.css");
		when(loginAspect).indexPageLoadedInDev(response, "en_GB");
		then(response).containsText(
			"<link rel=\"stylesheet\" title=\"blade-theme\" href=\"../v/dev/css/blade-theme/bundle.css\"/>");
	}
	
	
	@Test
	public void themesReferencedInCssTagsAreIncludedInGeneratedTags() throws Exception {
		given(aspect).containsFile("themes/standard/style.css")
			.and(aspect).indexPageHasContent("<@css.bundle theme=\"standard\"@/>")
			.and(commonTheme).containsFiles("style.css", "style_en.css")
			.and(standardTheme).containsFiles("style.css", "style_en.css");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(response).containsOrderedTextFragments(
			"<link rel=\"stylesheet\" href=\"../v/dev/css/common/bundle.css\"/>",
			"<link rel=\"stylesheet\" href=\"../v/dev/css/common_en/bundle.css\"/>",
			"<link rel=\"stylesheet\" title=\"standard\" href=\"../v/dev/css/standard/bundle.css\"/>",
			"<link rel=\"stylesheet\" title=\"standard\" href=\"../v/dev/css/standard_en/bundle.css\"/>");
	}
	
	@Test
	public void bladeThemesAreUsedInAWorkbenchEvenIfTheAspectDoesNotHaveThatTheme() throws Exception {
		given(aspect).containsFile("themes/standard/style.css")
			.and(workbench).indexPageHasContent("<@css.bundle theme=\"standard\"@/>\n")
			.and(commonTheme).containsFiles("style.css", "style_en.css")
			.and(standardTheme).containsFiles("style.css", "style_en.css");
		when(workbench).pageLoaded(response, "en");
		then(response).containsOrderedTextFragments(
				"<link rel=\"stylesheet\" href=\"../v/dev/css/common/bundle.css\"/>",
				"<link rel=\"stylesheet\" href=\"../v/dev/css/common_en/bundle.css\"/>",
				"<link rel=\"stylesheet\" title=\"standard\" href=\"../v/dev/css/standard/bundle.css\"/>",
				"<link rel=\"stylesheet\" title=\"standard\" href=\"../v/dev/css/standard_en/bundle.css\"/>");

	}
	
	@Test
	public void themesDefinedMultipleTimesOnlyHaveASingleReuqest() throws Exception {
		given(aspect).containsFile("themes/standard/style.css")
			.and(bladeset).containsFile("themes/standard/style.css")
			.and(bladeset).hasClass("appns/bs/Class")
			.and(blade).containsFile("themes/standard/style.css")
			.and(blade).hasClass("appns/bs/b1/Class")
			.and(aspect).indexPageHasContent("<@css.bundle theme=\"standard\"@/> appns.bs.Class  appns.bs.b1.Class ");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(response).containsTextOnce("<link rel=\"stylesheet\" title=\"standard\" href=\"../v/dev/css/standard/bundle.css\"/>");		
	}
	
	@Test
	public void requestsAreOnlyMadeForTheNamedTheme() throws Exception {
		given(aspect).containsFile("themes/standard/style.css")
			.and(aspect).containsFile("themes/theme1/style.css")
			.and(aspect).containsFile("themes/theme2/style.css")
			.and(aspect).indexPageHasContent("<@css.bundle theme=\"standard\"@/>");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(response).containsText("<link rel=\"stylesheet\" title=\"standard\" href=\"../v/dev/css/standard/bundle.css\"/>")
			.and(response).doesNotContainText("../v/dev/css/theme1/bundle.css")	
			.and(response).doesNotContainText("../v/dev/css/theme2/bundle.css");	
	}
	
	@Test
	public void requestsAreMadeForTheNamedThemeAndCommonIfItExists() throws Exception {
		given(aspect).containsFile("themes/common/style.css")
			.and(aspect).containsFile("themes/standard/style.css")
			.and(aspect).containsFile("themes/theme1/style.css")
			.and(aspect).containsFile("themes/theme2/style.css")
			.and(aspect).indexPageHasContent("<@css.bundle theme=\"standard\"@/>");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(response).containsOrderedTextFragments(
			"<link rel=\"stylesheet\" href=\"../v/dev/css/common/bundle.css\"/>",
			"<link rel=\"stylesheet\" title=\"standard\" href=\"../v/dev/css/standard/bundle.css\"/>")
			.and(response).doesNotContainText("../v/dev/css/theme1/bundle.css")	
			.and(response).doesNotContainText("../v/dev/css/theme2/bundle.css");
	}
	
	@Test
	public void bothThemeAndAlternateThemeCanBeUsed() throws Exception {
		given(aspect).containsFile("themes/standard/style.css")
    		.and(aspect).containsFile("themes/theme1/style.css")
    		.and(aspect).containsFile("themes/theme2/style.css")
    		.and(aspect).indexPageHasContent("<@css.bundle theme=\"standard\" alternateTheme=\"theme1,theme2\"@/>");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(response).containsOrderedTextFragments(
				"<link rel=\"stylesheet\" title=\"standard\" href=\"../v/dev/css/standard/bundle.css\"/>",	
				"<link rel=\"alternate stylesheet\" title=\"theme1\" href=\"../v/dev/css/theme1/bundle.css\"/>",	
				"<link rel=\"alternate stylesheet\" title=\"theme2\" href=\"../v/dev/css/theme2/bundle.css\"/>");	
	}
	
	@Test
	public void requestsAreOnlyMadeForTheAlternateNamedTheme() throws Exception {
		given(aspect).containsFile("themes/standard/style.css")
			.and(aspect).containsFile("themes/theme1/style.css")
			.and(aspect).containsFile("themes/theme2/style.css")
			.and(aspect).containsFile("themes/theme3/style.css")
			.and(aspect).indexPageHasContent("<@css.bundle alternateTheme=\"theme1\"@/>");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(response).containsText("<link rel=\"alternate stylesheet\" title=\"theme1\" href=\"../v/dev/css/theme1/bundle.css\"/>")
			.and(response).doesNotContainText("../v/dev/css/standard/bundle.css")	
			.and(response).doesNotContainText("../v/dev/css/theme2/bundle.css");	
	}
	
	@Test
	public void requestsForAlternateThemeDontIncludeCommon() throws Exception {
		given(aspect).containsFile("themes/common/style.css")
			.and(aspect).containsFile("themes/standard/style.css")
			.and(aspect).containsFile("themes/theme1/style.css")
			.and(aspect).indexPageHasContent("<@css.bundle alternateTheme=\"theme1\"@/>");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(response).containsText("<link rel=\"alternate stylesheet\" title=\"theme1\" href=\"../v/dev/css/theme1/bundle.css\"/>")
			.and(response).doesNotContainText("../v/dev/css/common/bundle.css");	
	}

	@Test
	public void themeAndAlternateThemeAttributesCanBeUsedAtTheSameTime() throws Exception {
		given(aspect).containsFile("themes/theme1/style.css")
			.and(aspect).containsFile("themes/theme2/style.css")
			.and(aspect).containsFile("themes/theme3/style.css")
			.and(aspect).indexPageHasContent("<@css.bundle theme=\"theme1\" alternateTheme=\"theme2\"@/>");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(response).containsOrderedTextFragments(
				"<link rel=\"stylesheet\" title=\"theme1\" href=\"../v/dev/css/theme1/bundle.css\"/>",
				"<link rel=\"alternate stylesheet\" title=\"theme2\" href=\"../v/dev/css/theme2/bundle.css\"/>");	
	}
	
	@Test
	public void alternateThemeCanBeACommaSeperatedList() throws Exception {
		given(aspect).containsFile("themes/theme1/style.css")
			.and(aspect).containsFile("themes/theme2/style.css")
			.and(aspect).containsFile("themes/theme3/style.css")
			.and(aspect).indexPageHasContent("<@css.bundle alternateTheme=\"theme2,theme3\"@/>");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(response).containsOrderedTextFragments(
				"<link rel=\"alternate stylesheet\" title=\"theme2\" href=\"../v/dev/css/theme2/bundle.css\"/>",	
				"<link rel=\"alternate stylesheet\" title=\"theme3\" href=\"../v/dev/css/theme3/bundle.css\"/>");	
	}
	
	@Test
	public void themeCannotBeACommaSeperatedList() throws Exception {
		given(aspect).containsFile("themes/theme1/style.css")
			.and(aspect).containsFile("themes/theme2/style.css")
			.and(aspect).containsFile("themes/theme3/style.css")
			.and(aspect).indexPageHasContent("<@css.bundle theme=\"theme2,theme3\"@/>");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(exceptions).verifyFormattedException(IOException.class, CssTagHandlerPlugin.INVALID_THEME_EXCEPTION, "theme2,theme3");	
	}
	
	@Test
	public void exceptionIsThrownIfTheThemeIsNotAnAvailableTheme() throws Exception {
		given(aspect).indexPageHasContent("<@css.bundle theme=\"theme\"@/>");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(exceptions).verifyFormattedException(IOException.class, CssTagHandlerPlugin.UNKNOWN_THEME_EXCEPTION, "theme");	
	}
	
	@Test
	public void exceptionIsThrownIfTheAlternateThemeIsNotAnAvailableTheme() throws Exception {
		given(aspect).indexPageHasContent("<@css.bundle alternateTheme=\"theme\"@/>");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(exceptions).verifyFormattedException(IOException.class, CssTagHandlerPlugin.UNKNOWN_THEME_EXCEPTION, "theme");	
	}
	
	@Test
	public void exceptionIsThrownIfTheSecondAlternateThemeIsNotAnAvailableTheme() throws Exception {
		given(aspect).containsFile("themes/theme1/style.css")
			.and(aspect).indexPageHasContent("<@css.bundle alternateTheme=\"theme1,theme2\"@/>");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(exceptions).verifyFormattedException(IOException.class, CssTagHandlerPlugin.UNKNOWN_THEME_EXCEPTION, "theme2");	
	}
	
}
