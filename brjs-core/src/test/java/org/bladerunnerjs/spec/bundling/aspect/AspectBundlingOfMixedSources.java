package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class AspectBundlingOfMixedSources extends SpecTest {
	private App app;
	private Aspect aspect;
	private JsLib sdkNamespaceLib, otherSdkNamespaceLib, sdkNodeJsLib;
	private StringBuffer response = new StringBuffer();

	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
		
		app = brjs.app("app1");
		aspect = app.aspect("default");

		sdkNamespaceLib = brjs.sdkLib("sdkNamespaceLib");
		otherSdkNamespaceLib = brjs.sdkLib("otherSdkNamespaceLib");
		sdkNodeJsLib = brjs.sdkLib("sdkNodeJsLib");
		
		given(sdkNamespaceLib).hasNamespacedJsPackageStyle()
			.and(otherSdkNamespaceLib).hasNamespacedJsPackageStyle()
			.and(sdkNodeJsLib).hasNodeJsPackageStyle();
	}
	
	// Namespace and NodeJS styles together
	@Test
	public void testThatNamespaceStyleLibraryCanProxyToNodeJsClassAndGetBundledInAspectIndexPage() throws Exception
	{
		given(sdkNodeJsLib).classFileHasContent("sdkNodeJsLib.Class1", "function empty() {};")
			.and(sdkNamespaceLib).classFileHasContent("sdkNamespaceLib.ProxyClass", "sdkNamespaceLib.ProxyClass = sdkNodeJsLib.Class1;")
			.and(aspect).indexPageHasContent("require('sdkNamespaceLib/ProxyClass')");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsOrderedTextFragments(
				
				// The sdkNodeJsLib is defined
				"define('sdkNodeJsLib/Class1', function(require, exports, module) {",
				"function empty() {};",
				"});",
				// The classes are both made available globally
				"mergePackageBlock(window, {\"sdkNamespaceLib\":{},\"sdkNodeJsLib\":{}});",
				// The namespaced style sdk class is assigned to the require of the nodeJsLib class  
				"sdkNodeJsLib.Class1 = require('sdkNodeJsLib/Class1');",
				"sdkNamespaceLib.ProxyClass = sdkNodeJsLib.Class1;",
				"define('sdkNamespaceLib/ProxyClass', function(require, exports, module) { module.exports = sdkNamespaceLib.ProxyClass; });");
	}
	
	// This test highlights an issue where the classes are being written to the bundle in an order which causes a JS syntax error
	// The behaviour I'm seeing is that the ProxyClass is being loaded before the class it's proxying is being defined
	@Ignore
	@Test
	public void testThatNamespaceStyleLibraryCanProxyToAnotherNamespaceLibraryClassAndGetBundledInAspectIndexPage() throws Exception
	{
		given(otherSdkNamespaceLib).classFileHasContent("otherSdkNamespaceLib.Class1", "otherSdkNamespaceLib.Class1 = function() { var x = 'original function'; };")
			.and(sdkNamespaceLib).classFileHasContent("sdkNamespaceLib.ProxyClass", "sdkNamespaceLib.ProxyClass = otherSdkNamespaceLib.Class1;")
			.and(aspect).indexPageHasContent("require('sdkNamespaceLib/ProxyClass')");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsOrderedTextFragments(
				// globalise the libraries first
				"mergePackageBlock(window, {\"sdkNamespaceLib\":{},\"otherSdkNamespaceLib\":{}});",
				// The class being proxied (the depended-on class) should be added first
				"otherSdkNamespaceLib.Class1 = function() { var x = 'original function'; };",
				"define('otherSdkNamespaceLib/Class1', function(require, exports, module) { module.exports = otherSdkNamespaceLib.Class1; });",
				// The library class doing the proxying should be added next
				"sdkNamespaceLib.ProxyClass = otherSdkNamespaceLib.Class1;",
				"define('sdkNamespaceLib/ProxyClass', function(require, exports, module) { module.exports = sdkNamespaceLib.ProxyClass; });");
	}
	
}
