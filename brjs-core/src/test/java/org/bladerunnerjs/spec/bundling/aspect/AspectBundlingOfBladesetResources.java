package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.Theme;
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
	// JS dependencies in HTML
	@Test
	public void bladesetClassesReferredToInAspectHTMlFilesAreBundled() throws Exception {
		given(bladeset).hasClasses("appns.bs.Class1", "appns.bs.Class2")
			.and(aspect).resourceFileRefersTo("html/view.html", "appns.bs.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.bs.Class1");
	}
	
	// HTML bundling
	@Test
	public void bladeHTMlFilesAreBundledIfTheirClassIsReferencedInsideIndexPage() throws Exception {
		given(bladeset).resourceFileContains("html/view.html", "<div id='appns.bs.view'>TESTCONTENT</div>")
			.and(bladeset).hasClass("appns.bs.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.Class1");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	@Test
	public void bladeHTMlFilesAreBundledIfAspectSrcRefersToBlade() throws Exception {
		given(bladeset).resourceFileContains("html/view.html", "<div id='appns.bs.view'>TESTCONTENT</div>")
			.and(bladeset).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClass("appns.bs.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.Class1")
			.and(aspect).classDependsOn("appns.Class1", "appns.bs.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}

	@Ignore // This test should pass?
	@Test
	public void bladeHTMlFilesAreBundledIfTheBladeIsReferredToByAspectHTMLFiles() throws Exception {
		given(bladeset).hasClass("appns.bs.Class1")
			.and(bladeset).resourceFileContains("html/view.html", "<div id='appns.bs.view'>TESTCONTENT</div>")
			.and(bladeset).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClass("appns.bs.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).resourceFileRefersTo("html/view.html", "appns.bs.Class1");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	@Test
	public void bladeHTMlFilesBundleFailsWithWrongNamespace() throws Exception {
		//given(logging).echoEnabled();
		given(bladeset).resourceFileContains("html/view.html", "<div id='appns.badnamespace.view'>TESTCONTENT</div>")
			.and(bladeset).hasClass("appns.bs.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.Class1");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "appns.badnamespace.view", "appns.bs.*");
	}
	
	@Test
	public void aspectHTMlFilesBundleFailsWithNoIDAttribute() throws Exception {
		given(bladeset).resourceFileContains("html/view.html", "<div>TESTCONTENT</div>")
			.and(bladeset).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClass("appns.bs.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.Class1");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "<div>", "appns.bs.*");
	}
	
	// ----------------------------------- C S S  -------------------------------------
	// TODO enable when we work on CSS Bundler
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
}
