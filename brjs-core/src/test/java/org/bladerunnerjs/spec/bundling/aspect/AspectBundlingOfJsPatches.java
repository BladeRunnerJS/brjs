package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


//TODO: why don't we get a namespace exception when we define classes outside of the namespace (e.g. 'appns' when the default namespace is 'appns')?
//TODO: we should fail-fast if somebody uses unquoted() in a logging assertion as it is only meant for exceptions where we can't easily ascertain the parameters
public class AspectBundlingOfJsPatches extends SpecTest {
	private App app;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			
		app = brjs.app("app1");
		aspect = app.aspect("default");
	}
	
	// ------------------------------- J S   P A T C H E S ----------------------------------
	@Test
	public void weDoNoIncludePatchesForClassesThatArentUsed() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src")
    		.and(aspect).hasClasses("appns.Class")
    		.and(aspect).indexPageRefersTo("new appns.Class()")
    		.and(brjs).containsFileWithContents("js-patches/appns/Class2.js", "appns.Class2.patch = function() {}");
    	when(app).requestReceived("/default-aspect/namespaced-js/bundle.js", response);
    	then(response).doesNotContainText("appns.Class2.patch = function() {}");
	}
}
