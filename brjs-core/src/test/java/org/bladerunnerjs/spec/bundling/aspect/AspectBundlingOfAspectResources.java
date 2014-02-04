package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Theme;
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
	
	@Test
	public void aspectClassesReferredToInAspectXMlFilesAreBundled() throws Exception {
		given(aspect).hasClasses("appns.Class1")
    		.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.Class1");
	}
	
	@Test
	public void aspectClassesReferredToInAspectHTMlFilesAreBundled() throws Exception {
		given(aspect).hasClasses("appns.Class1")
			.and(aspect).resourceFileRefersTo("html/view.html", "appns.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.Class1");
	}
	
	
	@Test
	public void aspectHTMlFilesAreBundled() throws Exception {
		given(aspect).resourceFileContains("html/view.html", "<div id='appns.view'>TESTCONTENT</div>");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	@Test
	public void aspectHTMlFilesBundleFailsWithWrongNamespace() throws Exception {
	
		//given(logging).echoEnabled();
		given(aspect).resourceFileContains("html/view.html", "<div id='xxxxx.view'>TESTCONTENT</div>");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "xxxxx.view", "appns.*");
	}
	
	@Test
	public void aspectHTMlFilesBundleFailsWithNoIDAttribute() throws Exception {
		given(aspect).resourceFileContains("html/view.html", "<div>TESTCONTENT</div>");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "<div>", "appns.*");
	}
	
	@Test
	public void aspectHTMlFilesBundleFailsWithDuplicateIDs() throws Exception {
		given(aspect).resourceFileContains("html/view.html", "<div id='appns.view'>TESTCONTENT</div>").
		and(aspect).resourceFileContains("html/view2.html", "<div id='appns.view'>TESTCONTENT</div>");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class,  "appns.view");
	}
	
	
	// TODO enable when we work on CSS Bundler
	@Ignore 
 	@Test
 	public void aspectCssFilesAreBundled() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(standardAspectTheme).containsFileWithContents("style.css", "ASPECT theme content");
 		when(app).requestReceived("/default-aspect/css/standard_css.bundle", response);
 		then(response).containsText("ASPECT theme content");
 	}
	
	@Test
	public void weBundleClassesReferredToByResourcesInAssetLocationsOfTheClassesWeAreBundling() throws Exception {
		given(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
    		.and(aspect).sourceResourceFileRefersTo("appns/config.xml", "appns.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.Class1", "appns.Class2");
	}
	
	@Test
	public void weDontBundleClassesReferredToByResourcesInAssetLocationsThatDoNotContainClassesWeAreBundling() throws Exception {
		given(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
    		.and(aspect).sourceResourceFileRefersTo("appns/pkg/config.xml", "appns.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.Class1");
	}
	
	@Test
	public void weBundleClassesReferredToByResourcesInAncestorAssetLocationsOfTheClassesWeAreBundling() throws Exception {
		given(aspect).hasClasses("appns.pkg.Class1", "appns.pkg.Class2")
			.and(aspect).indexPageRefersTo("appns.pkg.Class1")
    		.and(aspect).sourceResourceFileRefersTo("appns/config.xml", "appns.pkg.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.pkg.Class1", "appns.pkg.Class2");
	}
	
	
	@Test
	public void resourcesCanBeInTheRootOfTheResourcesDir() throws Exception {
		given(aspect).hasClasses("appns.Class1")
    		.and(aspect).resourceFileRefersTo("config.xml", "appns.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.Class1");
	}
	
	@Test
	public void resourcesCanBeInMultipleDirLevels() throws Exception {
		given(exceptions).arentCaught();
		
		given(aspect).hasClasses("appns.Class1")
    		.and(aspect).resourceFileRefersTo("config.xml", "appns.Class1")
    		.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1")
    		.and(aspect).resourceFileRefersTo("xml/dir1/config.xml", "appns.Class1");
    	when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
    	then(response).containsClasses("appns.Class1");
	}
}
