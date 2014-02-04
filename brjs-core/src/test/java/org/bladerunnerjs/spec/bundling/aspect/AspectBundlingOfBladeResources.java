package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.Theme;
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
	// JS dependencies
	@Test
	public void classesReferringToABladeInAspectHTMlFilesAreBundled() throws Exception {
		given(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(aspect).resourceFileRefersTo("html/view.html", "appns.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.bs.b1.Class1");
	}
	
	// HTML bundling
	@Test
	public void bladeHTMlFilesAreBundledIfTheirClassIsReferencedInsideIndexPage() throws Exception {
		given(blade).resourceFileContains("html/view.html", "<div id='appns.bs.b1.view'>TESTCONTENT</div>")
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	@Test
	public void bladeHTMlFilesAreBundledIfAspectSrcRefersToBlade() throws Exception {
		given(blade).resourceFileContains("html/view.html", "<div id='appns.bs.b1.view'>TESTCONTENT</div>")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.Class1")
			.and(aspect).classDependsOn("appns.Class1", "appns.bs.b1.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}

	@Ignore // This test should pass?
	@Test
	public void bladeHTMlFilesAreBundledIfTheBladeIsReferredToByAspectHTMLFiles() throws Exception {
		given(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).resourceFileContains("html/view.html", "<div id='appns.bs.b1.view'>TESTCONTENT</div>")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).resourceFileRefersTo("html/view.html", "appns.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	@Test
	public void bladeHTMlFilesBundleFailsWithWrongNamespace() throws Exception {
	
		//given(logging).echoEnabled();
		given(blade).resourceFileContains("html/view.html", "<div id='appns.bs.badnamespace.view'>TESTCONTENT</div>")
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "appns.bs.badnamespace.view", "appns.bs.b1.*");
	}
	
	@Test
	public void aspectHTMlFilesBundleFailsWithNoIDAttribute() throws Exception {
		given(blade).resourceFileContains("html/view.html", "<div>TESTCONTENT</div>");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "<div>", "appns.*");
	}
	
	
	// ----------------------------------- C S S  -------------------------------------
	// TODO enable when we work on CSS Bundler
	@Ignore 
 	@Test
 	public void bladeCssFilesAreBundledWhenReferencedInTheAspect() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(standardBladeTheme).containsFileWithContents("style.css", "BLADE theme content")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
 		when(app).requestReceived("/default-aspect/css/standard_css.bundle", response);
 		then(response).containsText("BLADE theme content");
 	}
}
