package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.plugin.bundler.js.NamespacedJsBundlerPlugin;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


//TODO: why don't we get a namespace exception when we define classes outside of the namespace (e.g. 'mypkg' when the default namespace is 'appns')?
//TODO: we should fail-fast if somebody uses unquoted() in a logging assertion as it is only meant for exceptions where we can't easily ascertain the parameters
public class AspectBundlingOfJsPatches extends SpecTest {
	private App app;
	private Aspect aspect;
	private JsLib sdkJsLib;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			
		app = brjs.app("app1");
			aspect = app.aspect("default");
			
			sdkJsLib = brjs.sdkLib();
	}
	
	// ------------------------------- J S   P A T C H E S ----------------------------------
//	TODO uncomment when jsPatches work is properly implemented
	@Ignore
	@Test
	public void weDoNotBundleNamespacedJsPatchesForLibraryClassesWhichAreNotReferenced() throws Exception {
		given(sdkJsLib).hasBeenCreated()
			.and(sdkJsLib).hasPackageStyle("src/sdkJsLib", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(sdkJsLib).hasClass("sdkJsLib.Class1")
//			.and(jsPatchesLib).hasPackageStyle("br/sdkJsLib", NamespacedJsBundlerPlugin.JS_STYLE)
//			.and(jsPatchesLib).hasClass("sdkJsLib.Class2");	
			.and(aspect).hasBeenCreated()
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(aspect).hasPackageStyle("src/mypkg", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClass("mypkg.Class1")
			.and(aspect).classRefersTo("mypkg.Class1", "sdkJsLib.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("sdkJsLib.Class1")
			.and(response).doesNotContainClasses("sdkJsLib.Class2");
	}
}
