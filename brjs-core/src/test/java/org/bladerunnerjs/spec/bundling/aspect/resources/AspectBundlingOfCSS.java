package org.bladerunnerjs.spec.bundling.aspect.resources;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.Theme;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class AspectBundlingOfCSS extends SpecTest {
	private App app;
	private Aspect aspect;
	private Theme standardAspectTheme, standardBladesetTheme, standardBladeTheme;
	private Bladeset bladeset;
	private Blade blade;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
		
			app = brjs.app("app1");
			aspect = app.aspect("default");
			standardAspectTheme = aspect.theme("standard");
			bladeset = app.bladeset("bs");
			standardBladesetTheme = bladeset.theme("standard");
			blade = bladeset.blade("b1");
			standardBladeTheme = blade.theme("standard");
	}
		
	// TODO enable when we work on CSS Bundler
	// Aspect
	@Ignore 
 	@Test
 	public void aspectCssFilesAreBundled() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(standardAspectTheme).containsFileWithContents("style.css", "ASPECT theme content");
 		when(app).requestReceived("/default-aspect/css/standard_css.bundle", response);
 		then(response).containsText("ASPECT theme content");
 	}
	
	// Bladeset
	@Ignore 
 	@Test
 	public void bladesetCssFilesAreBundledWhenReferencedInTheAspect() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClass("appns.bs.Class1")
			.and(standardBladesetTheme).containsFileWithContents("style.css", "BLADESET theme content")
			.and(aspect).indexPageRefersTo("appns.bs.Class1");
 		when(app).requestReceived("/default-aspect/css/standard_css.bundle", response);
 		then(response).containsText("BLADESET theme content");
 	}
	
	// Blade
 	public void bladeCssFilesAreBundledWhenReferencedInTheAspect() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(standardBladeTheme).containsFileWithContents("style.css", "BLADE theme content")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
 		when(app).requestReceived("/default-aspect/css/standard_css.bundle", response);
 		then(response).containsText("BLADE theme content");
 	}
}
