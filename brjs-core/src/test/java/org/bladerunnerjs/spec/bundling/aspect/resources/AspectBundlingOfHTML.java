package org.bladerunnerjs.spec.bundling.aspect.resources;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AspectBundlingOfHTML extends SpecTest {
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private JsLib sdkLib, userLib;
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
			sdkLib = brjs.sdkLib("br");
			userLib = app.jsLib("userLib");
	}
	
	// Aspect
	@Test
	public void aspectClassesReferredToInAspectHTMlFilesAreBundled() throws Exception {
		given(aspect).hasClasses("appns.Class1")
			.and(aspect).resourceFileRefersTo("html/view.html", "appns.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.Class1");
	}

	// Bladeset
	@Test
	public void bladesetClassesReferredToInAspectHTMlFilesAreBundled() throws Exception {
		given(bladeset).hasClasses("appns.bs.Class1", "appns.bs.Class2")
			.and(aspect).resourceFileRefersTo("html/view.html", "appns.bs.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.bs.Class1");
	}
	
	// Blade
	@Test
	public void bladeClassesReferredToInAspectHTMlFilesAreBundled() throws Exception {
		given(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(aspect).resourceFileRefersTo("html/view.html", "appns.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.bs.b1.Class1");
	}
	
	// SDK BRJS Lib
	@Test
	public void aspectCanBundleSdkLibHTMLResources() throws Exception {
		given(sdkLib).hasBeenCreated()
			.and(sdkLib).hasNamespacedJsPackageStyle()
			.and(sdkLib).containsFileWithContents("resources/html/workbench.html", "<div id='br.workbench-view'></div>")
			.and(sdkLib).hasClass("br.workbench.ui.Workbench")
			.and(aspect).containsFileWithContents("resources/aspect.html", "<div id='appns.aspect-view'></div>")
			.and(aspect).indexPageRefersTo("br.workbench.ui.Workbench");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(response).containsOrderedTextFragments(
				"<!-- workbench.html -->",
				"<div id='br.workbench-view'></div>",
				"<!-- aspect.html -->",
				"<div id='appns.aspect-view'></div>" );
	}
	
	
	// User library (specific to an app)
	@Test
	public void aspectCanBundleUserLibHTMLRresources() throws Exception {
		given(userLib).hasBeenCreated()
			.and(userLib).hasNamespacedJsPackageStyle()
			.and(userLib).containsFileWithContents("resources/html/userLib.html", "<div id='userLib.my-view'></div>")
			.and(userLib).hasClass("userLib.Class1")
			.and(aspect).containsFileWithContents("resources/aspect.html", "<div id='appns.aspect-view'></div>")
			.and(aspect).indexPageRefersTo("userLib.Class1");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(response).containsOrderedTextFragments(
				"<!-- userLib.html -->",
				"<div id='userLib.my-view'></div>",
				"<!-- aspect.html -->",
				"<div id='appns.aspect-view'></div>" );
	}
}
