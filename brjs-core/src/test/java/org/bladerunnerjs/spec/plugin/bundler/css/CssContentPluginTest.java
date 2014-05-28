package org.bladerunnerjs.spec.plugin.bundler.css;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.BladerunnerConf;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class CssContentPluginTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private JsLib nonConformantLib;
	private BladerunnerConf bladerunnerConf;
	private StringBuffer requestResponse = new StringBuffer();
	private Workbench workbench;
	private Blade blade;
	private Bladeset bladeset;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			nonConformantLib = app.jsLib("non-conformant-lib");
			bladerunnerConf = brjs.bladerunnerConf();
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			workbench = blade.workbench();
	}
	
	@Test
	public void cssFilesInResourcesAppearInTheCommonTheme() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFile("resources/style.css");
		when(aspect).requestReceived("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("resources/style.css");
	}
	
	@Test
	public void cssFilesInResourcesAppearDontAppearInAnyOtherThemes() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFile("resources/style.css");
		when(aspect).requestReceived("css/theme1/bundle.css", requestResponse);
		then(requestResponse).doesNotContainText("resources/style.css");
	}
	
	@Test
	public void cssFilesDeepWithinResourcesAppearInTheTheme() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFile("resources/dir1/dir2/style.css");
		when(aspect).requestReceived("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("resources/dir1/dir2/style.css");
	}
	
	@Test
	public void cssFilesInNonConformantLibrariesAppearInTheCommonTheme() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRequires(nonConformantLib)
			.and(nonConformantLib).containsFileWithContents("thirdparty-lib.manifest", "css: style1.css\n"+"exports: lib")
			.and(nonConformantLib).containsFile("style1.css")
			.and(nonConformantLib).containsFile("style2.css");
		when(aspect).requestReceived("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("style1.css")
			.and(requestResponse).doesNotContainText("style2.css");
	}

	@Test
	public void cssFilesInNonConformantLibrariesAppearAreAllBundledInCommonThemeWhenYouDontSpecifyCssManifestConfig() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRequires(nonConformantLib)
			.and(nonConformantLib).containsFileWithContents("thirdparty-lib.manifest", "js: foo.js\n"+"exports: lib")
			.and(nonConformantLib).containsFile("foo.js")
			.and(nonConformantLib).containsFileWithContents("style1.css", "style-1")
			.and(nonConformantLib).containsFileWithContents("style2.css", "style-2");
		when(aspect).requestReceived("css/common/bundle.css", requestResponse);
		then(requestResponse).containsLines("style-1", "style-2");
	}
	
	@Test
	public void cssFilesInNonConformantLibrariesDontAppearInAnyOtherThemes() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRequires(nonConformantLib)
			.and(nonConformantLib).containsFileWithContents("thirdparty-lib.manifest", "css: style.css\n"+"exports: lib")
			.and(nonConformantLib).containsFile("style.css");
		when(aspect).requestReceived("css/theme1/bundle.css", requestResponse);
		then(requestResponse).doesNotContainText("style.css");
	}
	
	@Test
	public void commonThemeCssFilesAppearInTheCommonTheme() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFile("themes/common/style.css");
		when(aspect).requestReceived("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("themes/common/style.css");
	}
	
	@Test
	public void commonThemeCssFilesDontAppearInAnyOtherThemes() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFile("themes/common/style.css");
		when(aspect).requestReceived("css/theme1/bundle.css", requestResponse);
		then(requestResponse).doesNotContainText("themes/common/style.css");
	}
	
	@Test
	public void nonCommonThemeCssFilesAppearInTheRelevantTheme() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFile("themes/theme1/style.css");
		when(aspect).requestReceived("css/theme1/bundle.css", requestResponse);
		then(requestResponse).containsText("themes/theme1/style.css");
	}
	
	@Test
	public void nonCommonThemeCssFilesDontAppearInAnyOtherThemes() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFile("themes/theme1/style.css");
		when(aspect).requestReceived("css/commmon/bundle.css", requestResponse);
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
		when(aspect).requestReceived("css/common/bundle.css", requestResponse);
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
		when(aspect).requestReceived("css/common/bundle.css", requestResponse);
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
		when(aspect).requestReceived("css/common/bundle.css", requestResponse);
		then(requestResponse).containsOrderedTextFragments("style1.css", "style2.css", "dir/style3.css");
	}
	
	@Test
	public void nonLocaleRequestsBundleNonLocaleOnlyStylesheets() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFiles("resources/style.css", "resources/style_de.css", "resources/style_de_DE.css", "resources/style_de_CH.css");
		when(aspect).requestReceived("css/common/bundle.css", requestResponse);
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
		when(aspect).requestReceived("css/common_de/bundle.css", requestResponse);
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
		when(aspect).requestReceived("css/common_de_DE/bundle.css", requestResponse);
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
		when(aspect).requestReceived("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("div {background:url(\"../../cssresource/aspect_default/theme_common/img.png\");}");
	}
	
	@Test
	public void referringToANestedImageCausesANestedCssResourceRequestToBeCreated() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFileWithContents("themes/common/style.css", "div {background:url('img/img.png');}");
		when(aspect).requestReceived("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("div {background:url(\"../../cssresource/aspect_default/theme_common/img/img.png\");}");
	}
	
	@Test
	public void referringToAParentImageCausesAParentCssResourceRequestToBeCreated() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFileWithContents("themes/common/foo/style.css", "div {background:url('../img.png');}");
		when(aspect).requestReceived("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("div {background:url(\"../../cssresource/aspect_default/theme_common/img.png\");}");
	}
	
	@Test
	public void weCanUseUTF8() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("UTF-8")
			.and().activeEncodingIs("UTF-8")
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFileWithContents("resources/style.css", "/* $£€ */");
		when(aspect).requestReceived("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("$£€");
	}
	
	@Test
	public void weCanUseLatin1() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("ISO-8859-1")
			.and().activeEncodingIs("ISO-8859-1")
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFileWithContents("resources/style.css", "/* $£ */");
		when(aspect).requestReceived("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("$£");
	}
	
	@Test
	public void weCanUseUnicodeFilesWithABomMarkerEvenWhenThisIsNotTheDefaultEncoding() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("ISO-8859-1")
			.and().activeEncodingIs("UTF-16")
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFileWithContents("resources/style.css", "/* $£€ */");
		when(aspect).requestReceived("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("$£€");
	}
	
	@Test
	public void themesFromAspectReferencedInCssTagsForWorbenchesAreIncludedInBundle() throws Exception {
		given(aspect.theme("standard")).hasBeenCreated()
			.and(aspect.theme("standard")).containsFileWithContents("file.css", "ASPECT CSS")
			.and(workbench).hasBeenCreated();
		when(workbench).requestReceived("css/standard/bundle.css", requestResponse);
		then(requestResponse).containsText("ASPECT CSS");

	}
	
	@Test
	public void aspectLanguageSpecifcFilesHaveToHaveToBePrefixedWithA_ToBeBundled() throws Exception {
		given(aspect).hasClass("appns/Class1")
    		.and(aspect).indexPageRefersTo("appns.Class1")
    		.and(aspect).containsFileWithContents("themes/standard/screen.css", "screen.css")
    		.and(aspect).containsFileWithContents("themes/standard/style_en.css", "style_en.css");
    	when(aspect).requestReceived("css/standard_en/bundle.css", requestResponse);
    	then(requestResponse).containsText("style_en.css")
    		.and(requestResponse).doesNotContainText("screen.css");
	}
	
	@Test
	public void bladeLanguageSpecifcFilesHaveToHaveToBePrefixedWithA_ToBeBundled() throws Exception {
		given(blade).hasClass("appns/bs/b1/Class1")
    		.and(aspect).indexPageRefersTo("appns.bs.b1.Class1")
    		.and(blade).containsFileWithContents("themes/standard/screen.css", "screen.css")
    		.and(blade).containsFileWithContents("themes/standard/style_en.css", "style_en.css");
    	when(aspect).requestReceived("css/standard_en/bundle.css", requestResponse);
    	then(requestResponse).containsText("style_en.css")
    		.and(requestResponse).doesNotContainText("screen.css");
	}
	
	@Test
	public void bladesetCssIsBundledEvenWhenThereIsNoJS() throws Exception {
		given(blade).hasClass("appns/bs/b1/Class1")
			.and(bladeset).containsFileWithContents("resources/style.css", "BLADESET STYLE")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceived("css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("BLADESET STYLE");
	}
	
	
}
