package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.SourceModulePatch;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


//TODO: why don't we get a namespace exception when we define classes outside of the namespace (e.g. 'appns' when the default namespace is 'appns')?
//TODO: we should fail-fast if somebody uses unquoted() in a logging assertion as it is only meant for exceptions where we can't easily ascertain the parameters
public class AspectBundlingOfJsPatches extends SpecTest {
	
	private App app;
	private Aspect aspect;
	private JsLib sdkJsLib;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			
		app = brjs.app("app1");
		aspect = app.aspect("default");
		sdkJsLib = brjs.sdkLib("sdkLib");
	}
	
	@Test
	public void weDoNoIncludePatchesForClassesThatArentUsed() throws Exception {
		given(sdkJsLib).hasClasses("sdkLib/Class", "sdkLib/AnotherClass")
    		.and(aspect).indexPageRequires("sdkLib/Class")
    		.and(brjs).containsFileWithContents("js-patches/sdkLib/AnotherClass.js", "sdkLib.AnotherClass.patch = function() {}");
    	when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then(response).doesNotContainText("sdkLib.AnotherClass.patch = function() {}")
    		.and(response).containsText("define('sdkLib/Class'");
	}
	
	
	@Test
	public void usefulLoggingIsOutputWhenAPatchIsApplied() throws Exception {
		given(logging).enabled()
			.and(sdkJsLib).hasClasses("sdkLib/Class", "sdkLib/AnotherClass")
    		.and(aspect).indexPageRequires("sdkLib/Class")
    		.and(brjs).containsFileWithContents("js-patches/sdkLib/Class.js", "sdkLib.Class.patch = function() {}");
    	when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then(response).containsText("sdkLib.Class.patch = ")
    		.and(logging).debugMessageReceived(SourceModulePatch.PATCH_APPLIED_MESSAGE, "sdkLib/Class", "js-patches/sdkLib/Class.js");
	}
	
	@Test
	public void updatedPatchesAreIncludedInEachBundle_WeDoNotCacheThePatchContent() throws Exception {
    	given(sdkJsLib).hasClasses("sdkLib/Class")
    		.and(aspect).indexPageRequires("sdkLib/Class")
    		.and(brjs).containsFileWithContents("js-patches/sdkLib/Class.js", "sdkLib.Class.patch = function() {}")
			.and(aspect).hasReceivedRequest("js/dev/combined/bundle.js");
		when(brjs).containsFileWithContents("js-patches/sdkLib/Class.js", "sdkLib.Class.newPatchMethod = function() {}")
    		.and(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then(response).containsText("sdkLib.Class.newPatchMethod = ");
	}
	
	@Test
	public void newRequiresAreIncludedInTheBundle_WeDontNotCacheThePatchRequires() throws Exception {
    	given(sdkJsLib).hasClasses("sdkLib/Class1", "sdkLib/Class2")
    		.and(aspect).indexPageRequires("sdkLib/Class1")
    		.and(brjs).containsFileWithContents("js-patches/sdkLib/Class1.js", "sdkLib.Class1.patch = function() {}")
			.and(aspect).hasReceivedRequest("js/dev/combined/bundle.js");
		when(brjs).containsFileWithContents("js-patches/sdkLib/Class1.js", "require('sdkLib/Class2')")
    		.and(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then(response).containsCommonJsClasses("sdkLib.Class2");
	}
	
}
