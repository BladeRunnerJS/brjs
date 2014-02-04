package org.bladerunnerjs.spec.plugin.bundler.css;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class CssBundlerPluginTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private JsLib nonConformantLib;
	private StringBuffer requestResponse = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			nonConformantLib = app.jsLib("non-conformant-lib");
	}
	
	@Test
	public void cssFilesInResourcesAppearInTheCommonTheme() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFile("resources/style.css");
		when(app).requestReceived("/default-aspect/css/common/bundle.css", requestResponse);
		then(requestResponse).containsOrderedTextFragments("resources/style.css");
	}
	
	@Test
	public void cssFilesInResourcesAppearDontAppearInAnyOtherThemes() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFile("resources/style.css");
		when(app).requestReceived("/default-aspect/css/theme1/bundle.css", requestResponse);
		then(requestResponse).doesNotContainText("resources/style.css");
	}
	
	@Test
	public void cssFilesDeepWithinResourcesAppearInTheTheme() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFile("resources/dir1/dir2/style.css");
		when(app).requestReceived("/default-aspect/css/common/bundle.css", requestResponse);
		then(requestResponse).containsOrderedTextFragments("resources/dir1/dir2/style.css");
	}
	
	@Test
	public void cssFilesInNonConformantLibrariesAppearInTheCommonTheme() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRequires(nonConformantLib)
			.and(nonConformantLib).containsFileWithContents("library.manifest", "css: style.css")
			.and(nonConformantLib).containsFile("style.css");
		when(app).requestReceived("/default-aspect/css/common/bundle.css", requestResponse);
		then(requestResponse).containsOrderedTextFragments("style.css");
	}
	
	@Test
	public void cssFilesInNonConformantLibrariesDontAppearInAnyOtherThemes() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRequires(nonConformantLib)
			.and(nonConformantLib).containsFileWithContents("library.manifest", "css: style.css")
			.and(nonConformantLib).containsFile("style.css");
		when(app).requestReceived("/default-aspect/css/theme1/bundle.css", requestResponse);
		then(requestResponse).doesNotContainText("style.css");
	}
	
	@Test
	public void commonThemeCssFilesAppearInTheCommonTheme() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFile("themes/common/style.css");
		when(app).requestReceived("/default-aspect/css/common/bundle.css", requestResponse);
		then(requestResponse).containsOrderedTextFragments("themes/common/style.css");
	}
	
	@Test
	public void commonThemeCssFilesDontAppearInAnyOtherThemes() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFile("themes/common/style.css");
		when(app).requestReceived("/default-aspect/css/theme1/bundle.css", requestResponse);
		then(requestResponse).doesNotContainText("themes/common/style.css");
	}
	
	@Test
	public void nonCommonThemeCssFilesAppearInTheRelevantTheme() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFile("themes/theme1/style.css");
		when(app).requestReceived("/default-aspect/css/theme1/bundle.css", requestResponse);
		then(requestResponse).containsOrderedTextFragments("themes/theme1/style.css");
	}
	
	@Test
	public void nonCommonThemeCssFilesDontAppearInAnyOtherThemes() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFile("themes/theme1/style.css");
		when(app).requestReceived("/default-aspect/css/commmon/bundle.css", requestResponse);
		then(requestResponse).doesNotContainText("themes/theme1/style.css");
	}
	
	// TODO: tests for locale specific themed css files
	
	@Test
	public void referringToAnImageCausesACssResourceRequestToBeCreated() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFileWithContents("themes/common/style.css", "div {background:url('img.png');}");
		when(app).requestReceived("/default-aspect/css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("div {background:url(\"../../cssresource/theme_common/img.png\");}");
	}
	
	@Test
	public void referringToANestedImageCausesANestedCssResourceRequestToBeCreated() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFileWithContents("themes/common/style.css", "div {background:url('img/img.png');}");
		when(app).requestReceived("/default-aspect/css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("div {background:url(\"../../cssresource/theme_common/img/img.png\");}");
	}
	
	@Test
	public void referringToAParentImageCausesAParentCssResourceRequestToBeCreated() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).containsFileWithContents("themes/common/foo/style.css", "div {background:url('../img.png');}");
		when(app).requestReceived("/default-aspect/css/common/bundle.css", requestResponse);
		then(requestResponse).containsText("div {background:url(\"../../cssresource/theme_common/img.png\");}");
	}
}
