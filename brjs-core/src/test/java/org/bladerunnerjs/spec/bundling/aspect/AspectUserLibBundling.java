package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AspectUserLibBundling extends SpecTest {
	private App app;
	private Aspect aspect;
	private JsLib userLib;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			
			app = brjs.app("app1");
			aspect = app.aspect("default");
			
			userLib = app.jsLib("userLib");
	}

	// ----------------------------- U S E R   J S   L I B S --------------------------------
	// This are 'BRJS conformant libraries'
	@Test
	public void aspectBundlesContainUserLibrLibsIfTheyAreReferencedInTheIndexPage() throws Exception {
		given(userLib).hasNamespacedJsPackageStyle()
			.and(userLib).hasClass("userLib.Class1")
			.and(aspect).indexPageRefersTo("userLib.Class1");
		when(aspect).requestReceived("js/dev/combined/bundle.js", response);
		then(response).containsClasses("userLib.Class1");
	}
	
	@Test
	public void aspectBundlesContainUserLibsIfTheyAreReferencedInAClass() throws Exception {
		given(userLib).hasBeenCreated()
			.and(userLib).hasNamespacedJsPackageStyle()
			.and(userLib).hasClass("userLib.Class1")
			.and(aspect).hasBeenCreated()
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.Class1")
			.and(aspect).classDependsOn("appns.Class1", "userLib.Class1");
		when(aspect).requestReceived("js/dev/combined/bundle.js", response);
		then(response).containsClasses("userLib.Class1");
	}
	
	@Test
	public void aspectBundlesContainUserLibsIfTheyAreRequiredInAClass() throws Exception {
		given(userLib).hasNodeJsPackageStyle()
			.and(userLib).hasClass("userLib/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).classRequires("appns/Class1", "userLib.Class1");
		when(aspect).requestReceived("js/dev/combined/bundle.js", response);
		then(response).containsNodeJsClasses("userLib.Class1");
	}

}
