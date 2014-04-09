package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AspectUserLibBundling extends SpecTest {
	private App app;
	private Aspect aspect, otherAspect;
	private JsLib userLib, otherUserLib;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			
			app = brjs.app("app1");
			aspect = app.aspect("default");
			otherAspect = app.aspect("other");
			
			userLib = app.jsLib("userLib");
			otherUserLib = app.jsLib("otherUserLib");
	}

	// ----------------------------- U S E R   J S   L I B S --------------------------------
	// This are 'BRJS conformant libraries'
	@Test
	public void aspectBundlesContainUserLibrLibsIfTheyAreReferencedInTheIndexPage() throws Exception {
		given(userLib).hasNamespacedJsPackageStyle()
			.and(userLib).hasClass("userLib.Class1")
			.and(aspect).indexPageRefersTo("userLib.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
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
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("userLib.Class1");
	}
	
	@Test
	public void aspectBundlesContainUserLibsIfTheyAreRequiredInAClass() throws Exception {
		given(userLib).hasNodeJsPackageStyle()
			.and(userLib).hasClass("userLib/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).classRequires("appns/Class1", "userLib.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsNodeJsClasses("userLib.Class1");
	}
	
	// dependencies across multiple aspects
	@Test
	public void canBundleDependenciesForAnotherAspectCorrectly() throws Exception {
		given(userLib).hasNodeJsPackageStyle()
			.and(userLib).hasClass("userLib/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).classRequires("appns/Class1", "userLib.Class1")
			.and(otherUserLib).hasNodeJsPackageStyle()
			.and(otherUserLib).hasClass("otherUserLib/Class1")
			.and(otherAspect).indexPageRefersTo("otherUserLib.Class1");
		when(app).requestReceived("/other-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsNodeJsClasses("otherUserLib.Class1")
			.and(response).doesNotContainText("userLib/Class1");
	}
}
