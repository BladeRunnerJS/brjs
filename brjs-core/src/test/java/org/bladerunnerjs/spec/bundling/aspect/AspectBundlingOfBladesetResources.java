package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.Theme;
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsBundlerContentPlugin;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class AspectBundlingOfBladesetResources extends SpecTest {
	private App app;
	private Aspect aspect;
	private Theme standardBladesetTheme;
	private Bladeset bladeset;
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
			standardBladesetTheme = bladeset.theme("standard");
	}
	
	// ----------------------------------- X M L -------------------------------------- 
	@Test
	public void bladesetClassesReferredToInAspectXMlFilesAreBundled() throws Exception {
		given(bladeset).hasClasses("appns.bs.Class1", "appns.bs.Class2")
    		.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.bs.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.bs.Class1");
	}

	// ---------------------------------  H T M L -------------------------------------
	@Test
	public void bladesetClassesReferredToInAspectHTMlFilesAreBundled() throws Exception {
		given(bladeset).hasClasses("appns.bs.Class1", "appns.bs.Class2")
			.and(aspect).resourceFileRefersTo("html/view.html", "appns.bs.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.bs.Class1");
	}
	
	// ----------------------------------- C S S  -------------------------------------
	// TODO enable when we work on CSS Bundler
	@Ignore 
 	@Test
 	public void bladesetCssFilesAreBundledWhenReferencedInTheAspect() throws Exception {
		given(aspect).hasPackageStyle("src/appns/bs", NamespacedJsBundlerContentPlugin.JS_STYLE)
			.and(bladeset).hasClass("appns.bs.Class1")
			.and(standardBladesetTheme).containsFileWithContents("style.css", "BLADESET theme content")
			.and(aspect).indexPageRefersTo("appns.bs.Class1");
 		when(app).requestReceived("/default-aspect/css/standard_css.bundle", response);
 		then(response).containsText("BLADESET theme content");
 	}
}
