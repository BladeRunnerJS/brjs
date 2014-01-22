package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.nodejs.NodeJsContentPlugin;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


//TODO: why don't we get a namespace exception when we define classes outside of the namespace (e.g. 'appns' when the default namespace is 'appns')?
//TODO: we should fail-fast if somebody uses unquoted() in a logging assertion as it is only meant for exceptions where we can't easily ascertain the parameters
public class AspectSdkJsLibraryBundling extends SpecTest {
	private App app;
	private Aspect aspect;
	private JsLib sdkLib;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			sdkLib = brjs.sdkLib();
	}

	@Test
	public void aspectBundlesContainsNodeStyleSdkLibsIfTheyAreReferencedInTheIndexPage() throws Exception {
		given(sdkLib).hasPackageStyle(NodeJsContentPlugin.JS_STYLE)
			.and(sdkLib).hasClass("br.SdkClass")
			.and(aspect).indexPageHasContent("require('br/SdkClass');");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsDefinedClasses("br/SdkClass");
	}
	
	@Test
	public void aspectBundlesContainsNamespaceStyleSdkLibsIfTheyAreReferencedInTheIndexPage() throws Exception {
		given(sdkLib).hasPackageStyle(NamespacedJsContentPlugin.JS_STYLE)
			.and(sdkLib).hasClass("br.SdkClass")
			.and(aspect).indexPageHasContent("require('br/SdkClass');");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsDefinedClasses("br/SdkClass");
	}
	
	
	@Test
	public void aspectBundlesContainSdkLibsIfTheyAreReferencedInAClass() throws Exception {
		given(aspect).hasClass("appns.AspectClass")
			.and(sdkLib).hasClass("br.SdkClass")
			.and(aspect).hasPackageStyle(NamespacedJsContentPlugin.JS_STYLE)
			.and(aspect).indexPageRefersTo("appns.AspectClass")
			.and(aspect).classRefersTo("appns.AspectClass", "br.SdkClass");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("br.SdkClass");
	}
	
	@Test
	public void aspectBundlesContainSdkLibsIfTheyAreRequiredInAClass() throws Exception {
		given(aspect).hasClass("appns.AspectClass")
			.and(sdkLib).hasClass("br.SdkClass")
			.and(aspect).indexPageRefersTo("appns.AspectClass")
			.and(aspect).classRequires("appns.AspectClass", "br.SdkClass");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("br.SdkClass");
	}
}
