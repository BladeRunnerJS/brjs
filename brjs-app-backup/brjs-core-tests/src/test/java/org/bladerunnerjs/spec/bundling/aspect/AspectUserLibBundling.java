package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.spec.engine.SpecTest;
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
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsNamespacedJsClasses("userLib.Class1");
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
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsNamespacedJsClasses("userLib.Class1");
	}
	
	@Test
	public void aspectBundlesContainUserLibsIfTheyAreRequiredInAClass() throws Exception {
		given(userLib).hasCommonJsPackageStyle()
			.and(userLib).hasClass("userLib/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).classRequires("appns/Class1", "userLib/Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("userLib.Class1");
	}

}
