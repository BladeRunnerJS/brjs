package org.bladerunnerjs.spec.bundling.aspect.resources;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AspectBundlingOfXML extends SpecTest {
	private App app;
	private Aspect aspect;
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
	}
	
	// Aspect XML
	@Test
	public void aspectClassesReferredToInAspectXMlFilesAreBundled() throws Exception {
		given(aspect).hasClasses("appns/Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsNodeJsClasses("appns.Class1");
	}

	@Test
	public void weBundleClassesReferredToByResourcesInAssetLocationsOfTheClassesWeAreBundling() throws Exception {
		given(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).sourceResourceFileRefersTo("appns/config.xml", "appns.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsNodeJsClasses("appns.Class1", "appns.Class2");
	}
	
	@Test
	public void weDontBundleClassesReferredToByResourcesInAssetLocationsThatDoNotContainClassesWeAreBundling() throws Exception {
		given(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).sourceResourceFileRefersTo("appns/pkg/config.xml", "appns.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsNodeJsClasses("appns.Class1");
	}
	
	@Test
	public void weBundleClassesReferredToByResourcesInAncestorAssetLocationsOfTheClassesWeAreBundling() throws Exception {
		given(aspect).hasClasses("appns/pkg/Class1", "appns/pkg/Class2")
			.and(aspect).indexPageRefersTo("appns.pkg.Class1")
			.and(aspect).sourceResourceFileRefersTo("appns/config.xml", "appns.pkg.Class2");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsNodeJsClasses("appns.pkg.Class1", "appns.pkg.Class2");
	}
	
	@Test
	public void resourcesCanBeInTheRootOfTheResourcesDir() throws Exception {
		given(aspect).hasClasses("appns/Class1")
			.and(aspect).resourceFileRefersTo("config.xml", "appns.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsNodeJsClasses("appns.Class1");
	}
	
	@Test
	public void resourcesCanBeInMultipleDirLevels() throws Exception {
		given(exceptions).arentCaught();
		
		given(aspect).hasClasses("appns/Class1")
			.and(aspect).resourceFileRefersTo("config.xml", "appns.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1")
			.and(aspect).resourceFileRefersTo("xml/dir1/config.xml", "appns.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsNodeJsClasses("appns.Class1");
	}
	
	// Bladeset XML
	@Test
	public void bladesetClassesReferredToInAspectXMlFilesAreBundled() throws Exception {
		given(bladeset).hasClasses("appns/bs/Class1", "appns/bs/Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.bs.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsNodeJsClasses("appns.bs.Class1");
	}

	
}
