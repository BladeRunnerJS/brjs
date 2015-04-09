package org.bladerunnerjs.spec.observer;

import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.plugin.jsdoc.JsDocObserver;
import org.junit.Test;

public class JsDocModelObserverTest extends SpecTest {
	
	@Test 
	public void placeholdersAreCreatedWhenANewAppIsDiscovered() throws Exception {
		given(testSdkDirectory).containsFileWithContents("apps/app1/src/MyClass.js", "// my class")
			.and(brjs).hasModelObserverPlugins(new JsDocObserver())
			.and(testSdkDirectory).containsFileWithContents("sdk/jsdoc-toolkit-resources/jsdoc-placeholders/index.html", "PLACEHOLDER");
		when(brjs).hasBeenCreated();
			/* and */ brjs.apps();
		then(brjs).hasDir("generated/app/app1/jsdoc")
			.and(brjs.app("app1").storageDir("jsdoc")).containsFileWithContents("index.html", "PLACEHOLDER");
	}
	
	@Test 
	public void placeholdersAreCreatedWhenANewAppIsCreatedViaTheModel() throws Exception {
		given(brjs).hasModelObserverPlugins(new JsDocObserver())
			.and(brjs).hasBeenCreated()
			.and(brjs.sdkTemplateGroup("default")).templateGroupCreated()
    		.and(testSdkDirectory).containsFileWithContents("sdk/jsdoc-toolkit-resources/jsdoc-placeholders/index.html", "PLACEHOLDER");
		when( brjs.app("app1" ) ).create();
		then(brjs).hasDir("generated/app/app1/jsdoc")
			.and(brjs.app("app1").storageDir("jsdoc")).containsFileWithContents("index.html", "PLACEHOLDER");
	}
	
}
