package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Theme;
import org.bladerunnerjs.plugin.bundler.js.NamespacedJsBundlerPlugin;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class AspectBundlingOfAspectResources extends SpecTest {
	private App app;
	private Aspect aspect;
	private Theme standardAspectTheme;
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
	}
	
	// ----------------------------------- X M L -------------------------------------- 
	@Test
	public void aspectClassesReferredToInAspectXMlFilesAreBundled() throws Exception {
		given(aspect).hasClasses("mypkg.Class1")
    		.and(aspect).resourceFileRefersTo("xml/config.xml", "mypkg.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.Class1");
	}
	
	// ---------------------------------  H T M L -------------------------------------
	@Test
	public void aspectClassesReferredToInAspectHTMlFilesAreBundled() throws Exception {
		given(aspect).hasClasses("mypkg.Class1")
			.and(aspect).resourceFileRefersTo("html/view.html", "mypkg.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.Class1");
	}
	
	// ----------------------------------- C S S  -------------------------------------
	// TODO enable when we work on CSS Bundler
	@Ignore 
 	@Test
 	public void aspectCssFilesAreBundled() throws Exception {
		given(aspect).hasPackageStyle("src/mypkg", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(standardAspectTheme).containsFileWithContents("style.css", "ASPECT theme content");
 		when(app).requestReceived("/default-aspect/css/standard_css.bundle", response);
 		then(response).containsText("ASPECT theme content");
 	}
}
