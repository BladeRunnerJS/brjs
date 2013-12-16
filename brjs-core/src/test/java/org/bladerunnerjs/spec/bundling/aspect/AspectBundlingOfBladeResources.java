package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.Theme;
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsBundlerContentPlugin;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class AspectBundlingOfBladeResources extends SpecTest {
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private Theme standardBladeTheme;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
		
			app = brjs.app("app1");
			aspect = app.aspect("default");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			standardBladeTheme = blade.theme("standard");
	}
	
	// ----------------------------------- X M L -------------------------------------- 
	@Test
	public void classesReferringToABladeInAspectXMlFilesAreBundled() throws Exception {
		given(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
    		.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.bs.b1.Class1");
	}

	// ---------------------------------  H T M L -------------------------------------
	@Test
	public void classesReferringToABladeInAspectHTMlFilesAreBundled() throws Exception {
		given(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(aspect).resourceFileRefersTo("html/view.html", "appns.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.bs.b1.Class1");
	}
	
	// ----------------------------------- C S S  -------------------------------------
	// TODO enable when we work on CSS Bundler
	@Ignore 
 	@Test
 	public void bladeCssFilesAreBundledWhenReferencedInTheAspect() throws Exception {
		given(aspect).hasPackageStyle("src/appns/bs/b1", NamespacedJsBundlerContentPlugin.JS_STYLE)
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(standardBladeTheme).containsFileWithContents("style.css", "BLADE theme content")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
 		when(app).requestReceived("/default-aspect/css/standard_css.bundle", response);
 		then(response).containsText("BLADE theme content");
 	}
}
