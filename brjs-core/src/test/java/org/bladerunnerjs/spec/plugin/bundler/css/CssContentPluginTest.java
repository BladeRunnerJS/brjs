package org.bladerunnerjs.spec.plugin.bundler.css;

import java.io.File;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.BladerunnerConf;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class CssContentPluginTest extends SpecTest {
	private App app;
	private AppConf appConf;
	private Aspect aspect;
	private File commonTheme;
	private File mainTheme;
	private File bladeMainTheme;
	private JsLib brBoostrapLib;
	private JsLib nonConformantLib;
	private JsLib nonConformantLib2;
	private BladerunnerConf bladerunnerConf;
	private StringBuffer requestResponse = new StringBuffer();
	private Workbench workbench;
	private Blade blade;
	private Bladeset bladeset;
	private Bladeset defaultBladeset;
	private Blade bladeInDefaultBladeset;
	private Aspect defaultAspect;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			appConf = app.appConf();
			aspect = app.aspect("default");
			defaultAspect = app.defaultAspect();
			commonTheme = aspect.file("themes/common");
			mainTheme = aspect.file("themes/main");
			brBoostrapLib = brjs.sdkLib("br-bootstrap");
			nonConformantLib = app.jsLib("non-conformant-lib");
			nonConformantLib2 = app.jsLib("non-conformant-lib2");
			bladerunnerConf = brjs.bladerunnerConf();
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			bladeMainTheme = blade.file("themes/main");
			workbench = blade.workbench();
			defaultBladeset = app.defaultBladeset();
			bladeInDefaultBladeset = defaultBladeset.blade("b1");
	}
	
	@Test
	public void ifThereAreNoCssFilesThenNoRequestsWillBeGenerated() throws Exception {
		given(aspect).indexPageHasContent("index page");
		then(aspect).prodAndDevRequestsForContentPluginsAreEmpty("css");
	}
	
	@Test
	public void onlyCssFilesAreValid() throws Exception {
		given(commonTheme).containsFile("style.style")
		.and(aspect).indexPageHasContent("index page");
		then(aspect).prodAndDevRequestsForContentPluginsAreEmpty("css");
	}
	
	@Test
	public void ifThereAreCssFilesThenRequestsWillBeGenerated() throws Exception {
		given(commonTheme).containsFile("style.css")
			.and(aspect).indexPageHasContent("index page");
		then(aspect).prodAndDevRequestsForContentPluginsAre("css", "css/common/bundle.css");
	}
	
	@Test
	public void resourceCssFilesAreTreatedAsPartOfTheCommonTheme() throws Exception {
		given(aspect).containsResourceFile("style.css")
			.and(aspect).indexPageHasContent("index page");
		then(aspect).prodAndDevRequestsForContentPluginsAre("css", "css/common/bundle.css");
	}
	
	@Test
	public void ifThereAreLanguageSpecificCssFilesThenLanguageSpecificRequestsWillBeGenerated() throws Exception {
		given(commonTheme).containsFile("style_en.css")
			.and(aspect).indexPageHasContent("index page");
		then(aspect).prodAndDevRequestsForContentPluginsAre("css", "css/common_en/bundle.css");
	}
	
	@Test
	public void ifThereAreLocaleSpecificCssFilesThenLocaleSpecificRequestsWillBeGenerated() throws Exception {
		given(appConf).supportsLocales("en", "en_GB")
			.and(commonTheme).containsFile("style_en_GB.css")
			.and(aspect).indexPageHasContent("index page");
		then(aspect).prodAndDevRequestsForContentPluginsAre("css", "css/common_en_GB/bundle.css");
	}
	
	@Test
	public void ifThereIsAMixOfLocalesCssFilesThenCorrespondingRequestsWillBeGenerated() throws Exception {
		given(appConf).supportsLocales("en", "en_GB")
			.and(commonTheme).containsFile("style.css")
			.and(commonTheme).containsFile("style_en_GB.css")
			.and(aspect).indexPageHasContent("index page");
		then(aspect).prodAndDevRequestsForContentPluginsAre("css", "css/common/bundle.css", "css/common_en_GB/bundle.css");
	}
	
	@Test
	public void ifAllLocalesCssFilesExistThenAllRequestsWillBeGenerated() throws Exception {
		given(appConf).supportsLocales("en", "en_GB")
			.and(commonTheme).containsFile("style.css")
			.and(commonTheme).containsFile("style_en.css")
			.and(commonTheme).containsFile("style_en_GB.css")
			.and(aspect).indexPageHasContent("index page");
		then(aspect).prodAndDevRequestsForContentPluginsAre("css", "css/common/bundle.css", "css/common_en/bundle.css", "css/common_en_GB/bundle.css");
	}
	
	@Test
	public void thereBeingCssFilesForLocalesThatArentSupportDoesNotAffectTheGeneratedRequests() throws Exception {
		given(commonTheme).containsFile("style.css")
			.and(commonTheme).containsFile("style_en_GB.css")
			.and(aspect).indexPageHasContent("index page");
		then(aspect).prodAndDevRequestsForContentPluginsAre("css", "css/common/bundle.css");
	}
	
	@Test
	public void requestsForAllTheThemesDefinedWithinTheAspectAreGenerated() throws Exception {
		given(commonTheme).containsFile("style.css")
			.and(mainTheme).containsFile("style_en.css")
			.and(aspect).indexPageHasContent("index page");
		then(aspect).prodAndDevRequestsForContentPluginsAre("css", "css/common/bundle.css", "css/main_en/bundle.css");
	}
	
	// TODO: this was one of a whole raft of useful tests that was previously deleted by James T. -- will see if he can investigate why this one no longer works
	@Ignore
	@Test
	public void thereBeingBladeThemesThatArentDefinedInTheAspectDoesNotAffectTheGeneratedRequests() throws Exception {
		given(commonTheme).containsFile("style.css")
			.and(bladeMainTheme).containsFile("style.css")
			.and(aspect).indexPageRequires("appns/bs/b1/Class")
			.and(blade).hasClass("appns/bs/b1/Class");
		then(aspect).prodAndDevRequestsForContentPluginsAre("css", "css/common/bundle.css");
	}
	
	@Test
	public void bladeThemesAreHoweverTakenIntoConsiderationIfTheyIdentifyAdditionalLocaleVariants() throws Exception {
		given(mainTheme).containsFile("style.css")
			.and(bladeMainTheme).containsFile("style_en.css")
			.and(aspect).indexPageRequires("appns/bs/b1/Class")
			.and(blade).hasClass("appns/bs/b1/Class");
		then(aspect).prodAndDevRequestsForContentPluginsAre("css", "css/main_en/bundle.css", "css/main/bundle.css");
	}
	
	@Test
	public void cssFilesInResourcesAppearInTheCommonTheme() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsResourceFile("style.css");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("style.css");
	}
	
	@Test
	public void cssFilesInResourcesAppearDontAppearInAnyOtherThemes() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsResourceFile("style.css");
		when(aspect).requestReceivedInDev("css/theme1/bundle.css", requestResponse);
		then(requestResponse).doesNotContainText("resources/style.css");
	}
	
	@Test
	public void cssFilesDeepWithinResourcesAppearInTheTheme() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsResourceFile("dir1/dir2/style.css");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("dir1/dir2/style.css");
	}
	
	@Test
	public void cssFilesInNonConformantLibrariesAppearInTheCommonTheme() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRequires(nonConformantLib)
			.and(nonConformantLib).containsFileWithContents("thirdparty-lib.manifest", "css: style1.css\n"+"exports: lib")
			.and(nonConformantLib).containsFile("style1.css")
			.and(nonConformantLib).containsFile("style2.css");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("style1.css")
			.and(requestResponse).doesNotContainText("style2.css");
	}

	@Test
	public void cssFilesForTransitivelyDependantThirdpartyLibrariesAppearInTheCommonTheme() throws Exception {
		given(aspect).indexPageRequires(nonConformantLib2)
			.and(nonConformantLib2).containsFileWithContents("thirdparty-lib.manifest", "depends: non-conformant-lib\n"+"exports: lib")
			.and(nonConformantLib).containsFileWithContents("thirdparty-lib.manifest", "css: style1.css\n"+"exports: lib")
			.and(nonConformantLib).containsFile("style1.css");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("style1.css");
	}
	
	@Test
	public void cssFilesForDependenciesOfBoostrapAppearInTheCommonTheme() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRequires("appns/Class1")
			.and(brBoostrapLib).containsFileWithContents("thirdparty-lib.manifest", "depends: non-conformant-lib\n"+"exports: lib")
			.and(nonConformantLib).containsFileWithContents("thirdparty-lib.manifest", "css: style1.css\n"+"exports: lib")
			.and(nonConformantLib).containsFile("style1.css");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("style1.css");
	}
	
	@Test
	public void cssFilesForDependantThirdpartyLibrariesAppearInTheCommonTheme() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRequires(nonConformantLib)
			.and(nonConformantLib).containsFileWithContents("thirdparty-lib.manifest", "css: style1.css\n"+"exports: lib")
			.and(nonConformantLib).containsFile("style1.css");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("style1.css");
	}

	@Test
	public void cssFilesInNonConformantLibrariesAppearAreAllBundledInCommonThemeWhenYouDontSpecifyCssManifestConfig() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRequires(nonConformantLib)
			.and(nonConformantLib).containsFileWithContents("thirdparty-lib.manifest", "js: foo.js\n"+"exports: lib")
			.and(nonConformantLib).containsFile("foo.js")
			.and(nonConformantLib).containsFileWithContents("style1.css", "style-1")
			.and(nonConformantLib).containsFileWithContents("style2.css", "style-2");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsLines("style-1", "style-2");
	}
	
	@Test
	public void cssFilesInNonConformantLibrariesDontAppearInAnyOtherThemes() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRequires(nonConformantLib)
			.and(nonConformantLib).containsFileWithContents("thirdparty-lib.manifest", "css: style.css\n"+"exports: lib")
			.and(nonConformantLib).containsFile("style.css");
		when(aspect).requestReceivedInDev("css/theme1/bundle.css", requestResponse);
		then(requestResponse).doesNotContainText("style.css");
	}
	
	@Test
	public void commonThemeCssFilesAppearInTheCommonTheme() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFile("themes/common/style.css");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("themes/common/style.css");
	}
	
	@Test
	public void commonThemeCssFilesDontAppearInAnyOtherThemes() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFile("themes/common/style.css");
		when(aspect).requestReceivedInDev("css/theme1/bundle.css", requestResponse);
		then(requestResponse).doesNotContainText("themes/common/style.css");
	}
	
	@Test
	public void nonCommonThemeCssFilesAppearInTheRelevantTheme() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFile("themes/theme1/style.css");
		when(aspect).requestReceivedInDev("css/theme1/bundle.css", requestResponse);
		then(requestResponse).containsText("themes/theme1/style.css");
	}
	
	@Test
	public void nonCommonThemeCssFilesDontAppearInAnyOtherThemes() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFile("themes/theme1/style.css");
		when(aspect).requestReceivedInDev("css/commmon/bundle.css", requestResponse);
		then(requestResponse).doesNotContainText("themes/theme1/style.css");
	}
	
	@Test
	public void allNonNestedCssFilesInNonConformantLibrariesAppearIfLeftUnspecified() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRequires(nonConformantLib)
			.and(nonConformantLib).containsFileWithContents("thirdparty-lib.manifest", "js: script.js\n"+"exports: lib")
			.and(nonConformantLib).containsFile("script.js")
			.and(nonConformantLib).containsFile("style1.css")
			.and(nonConformantLib).containsFile("style2.css")
			.and(nonConformantLib).containsFile("dir/style3.css");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsOrderedTextFragments("style1.css", "style2.css")
			.and(requestResponse).doesNotContainText("dir/style3.css");
	}
	
	@Test
	public void allCssFilesInNonConformantLibrariesAppearIfAildcardIsExplicitlySpecified() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRequires(nonConformantLib)
			.and(nonConformantLib).containsFileWithContents("thirdparty-lib.manifest", "css: \"*.css\"\n"+"exports: lib")
			.and(nonConformantLib).containsFile("style1.css")
			.and(nonConformantLib).containsFile("style2.css")
			.and(nonConformantLib).containsFile("dir/style3.css");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsOrderedTextFragments("style1.css", "style2.css")
			.and(requestResponse).doesNotContainText("dir/style3.css");
	}
	
	@Test
	public void allCssFilesInNonConformantLibrariesAppearIfADeepWildcardIsExplicitlySpecified() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRequires(nonConformantLib)
			.and(nonConformantLib).containsFileWithContents("thirdparty-lib.manifest", "css: \"**/*.css\"\n"+"exports: lib")
			.and(nonConformantLib).containsFile("style1.css")
			.and(nonConformantLib).containsFile("style2.css")
			.and(nonConformantLib).containsFile("dir/style3.css");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsOrderedTextFragments("style1.css", "style2.css", "dir/style3.css");
	}
	
	@Test
	public void nonLocaleRequestsBundleNonLocaleOnlyStylesheets() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFiles("resources/style.css", "resources/style_de.css", "resources/style_de_DE.css", "resources/style_de_CH.css");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("style.css")
			.and(requestResponse).doesNotContainText("style_de.css")
			.and(requestResponse).doesNotContainText("style_de_DE.css")
			.and(requestResponse).doesNotContainText("style_de_CH.css");
	}
	
	@Test
	public void languageRequestsBundleLanguageOnlyStylesheets() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFiles("resources/style.css", "resources/style_de.css", "resources/style_de_DE.css", "resources/style_de_CH.css");
		when(aspect).requestReceivedInDev("css/common_de/bundle.css", requestResponse);
		then(requestResponse).containsText("style_de.css")
			.and(requestResponse).doesNotContainText("style.css")
			.and(requestResponse).doesNotContainText("style_de_DE.css")
			.and(requestResponse).doesNotContainText("style_de_CH.css");
	}
	
	@Test
	public void localeRequestsBundleLocaleOnlyStylesheets() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFiles("resources/style.css", "resources/style_de.css", "resources/style_de_DE.css", "resources/style_de_CH.css");
		when(aspect).requestReceivedInDev("css/common_de_DE/bundle.css", requestResponse);
		then(requestResponse).containsText("style_de_DE.css")
			.and(requestResponse).doesNotContainText("style.css")
			.and(requestResponse).doesNotContainText("style_de.css")
			.and(requestResponse).doesNotContainText("style_de_CH.css");
	}
	
	
	@Test
	public void referringToAnImageCausesACssResourceRequestToBeCreated() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFileWithContents("themes/common/style.css", "div {background:url('img.png');}");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("div {background:url('../../cssresource/aspect_default/theme_common/img.png');}");
	}
	
	@Test
	public void referringToANestedImageCausesANestedCssResourceRequestToBeCreated() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFileWithContents("themes/common/style.css", "div {background:url('img/img.png');}");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("div {background:url('../../cssresource/aspect_default/theme_common/img/img.png');}");
	}
	
	@Test
	public void referringToAParentImageCausesAParentCssResourceRequestToBeCreated() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFileWithContents("themes/common/foo/style.css", "div {background:url('../wibble/img.png');}");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("div {background:url('../../cssresource/aspect_default/theme_common/wibble/img.png');}");
	}

	@Test
	public void weCanUseUTF8() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("UTF-8")
			.and().activeEncodingIs("UTF-8")
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsResourceFileWithContents("style.css", "/* $£€ */");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("$£€");
	}
	
	@Test
	public void weCanUseLatin1() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("ISO-8859-1")
			.and().activeEncodingIs("ISO-8859-1")
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsResourceFileWithContents("style.css", "/* $£ */");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("$£");
	}
	
	@Test
	public void weCanUseUnicodeFilesWithABomMarkerEvenWhenThisIsNotTheDefaultEncoding() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("ISO-8859-1")
			.and().activeEncodingIs("UTF-16")
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFileWithContents("resources/style.css", "/* $£€ */");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("$£€");
	}
	
	@Test
	public void themesFromAspectReferencedInCssTagsForWorbenchesAreIncludedInBundle() throws Exception {
		given(aspect).containsFileWithContents("themes/standard/file.css", "ASPECT CSS")
			.and(workbench).hasBeenCreated();
		when(workbench).requestReceivedInDev("css/standard/bundle.css", requestResponse);
		then(requestResponse).containsText("ASPECT CSS");
	}
	
	@Test
	public void aspectLanguageSpecifcFilesHaveToHaveToBePrefixedWithA_ToBeBundled() throws Exception {
		given(aspect).hasClass("appns/Class1")
    		.and(aspect).indexPageRefersTo("appns.Class1")
    		.and(aspect).containsFileWithContents("themes/standard/screen.css", "screen.css")
    		.and(aspect).containsFileWithContents("themes/standard/style_en.css", "style_en.css");
    	when(aspect).requestReceivedInDev("css/standard_en/bundle.css", requestResponse);
    	then(requestResponse).containsText("style_en.css")
    		.and(requestResponse).doesNotContainText("screen.css");
	}
	
	@Test
	public void bladeLanguageSpecifcFilesHaveToHaveToBePrefixedWithA_ToBeBundled() throws Exception {
		given(blade).hasClass("appns/bs/b1/Class1")
    		.and(aspect).indexPageRefersTo("appns.bs.b1.Class1")
    		.and(blade).containsFileWithContents("themes/standard/screen.css", "screen.css")
    		.and(blade).containsFileWithContents("themes/standard/style_en.css", "style_en.css");
    	when(aspect).requestReceivedInDev("css/standard_en/bundle.css", requestResponse);
    	then(requestResponse).containsText("style_en.css")
    		.and(requestResponse).doesNotContainText("screen.css");
	}
	
	@Test
	public void bladesetCssIsBundledEvenWhenThereIsNoJS() throws Exception {
		given(blade).hasClass("appns/bs/b1/Class1")
			.and(bladeset).containsResourceFileWithContents("style.css", "BLADESET STYLE")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("BLADESET STYLE");
	}
	
	@Test
	public void cssFilenamesCanContainAnd_IfTheyDontMatchTheStructureOfALocaleFilename() throws Exception {
		given(aspect).hasClass("appns/Class1")
    		.and(aspect).indexPageRefersTo("appns.Class1")
    		.and(aspect).containsFileWithContents("themes/standard/style_sheet.css", "style_sheet.css")
			.and(aspect).containsFileWithContents("themes/standard/stylesheet_1.css", "stylesheet_1.css")
			.and(aspect).containsFileWithContents("themes/standard/stylesheet_ab.css", "stylesheet_ab.css")
			.and(aspect).containsFileWithContents("themes/standard/stylesheet_ab_cd.css", "stylesheet_ab_cd.css");
    	when(aspect).requestReceivedInDev("css/standard/bundle.css", requestResponse);
    	then(requestResponse).containsText("style_sheet.css")
    		.and(requestResponse).containsText("stylesheet_1.css")
    		.and(requestResponse).doesNotContainText("stylesheet_ab.css")
    		.and(requestResponse).doesNotContainText("stylesheet_ab_cd.css");
	}
	
	@Test
	public void rewrittenImageURLsCanHaveAnyExcetion() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFileWithContents("themes/common/foo/style.css", "div {background:url('../wibble/image.with-my-super-cool-extension');}");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("div {background:url('../../cssresource/aspect_default/theme_common/wibble/image.with-my-super-cool-extension');}");
	}
	
	@Test
	public void invalidCssDoesntPreventOtherContentBeingBundledAndRewritten() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFileWithContents("themes/common/style1.css", "asdaasdjhsadfohaahcsjhfw   div {background:url('image1.png');}")
			.and(aspect).containsFileWithContents("themes/common/style2.css", "div {background:url('image2.png');}")
			.and(aspect).containsFileWithContents("themes/common/style3.css", "div {background:url('image3.png'); jhasdjadsja }");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
				"asdaasdjhsadfohaahcsjhfw   div {background:url('../../cssresource/aspect_default/theme_common/image1.png');}",
				"div {background:url('../../cssresource/aspect_default/theme_common/image2.png');}",
				"div {background:url('../../cssresource/aspect_default/theme_common/image3.png'); jhasdjadsja }"
		);
	}
	
	@Test
	public void bladeCSSInDefaultBladesetCanBeBundled() throws Exception {
		given(bladeInDefaultBladeset).hasClass("appns/b1/BladeClass")
			.and(bladeInDefaultBladeset).containsFileWithContents("themes/common/style.css", "blade css")
			.and(aspect).indexPageRequires("appns/b1/BladeClass");
		when(aspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("blade css");
	}
	
	@Test
	public void CSSInDefaultAspectCanBeBundled() throws Exception {
		given(defaultAspect).hasClass("appns/AspectClass")
			.and(defaultAspect).containsFileWithContents("themes/common/style.css", "aspect css")
			.and(defaultAspect).indexPageRequires("appns/AspectClass");
		when(defaultAspect).requestReceivedInDev("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("aspect css");
	}
	
}
