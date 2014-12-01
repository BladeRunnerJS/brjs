package org.bladerunnerjs.spec.observer;

import org.bladerunnerjs.plugin.plugins.jsdoc.JsDocObserver;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
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
			.and(brjs.templateGroup("default").template("app")).hasBeenCreated()
			.and(brjs.templateGroup("default").template("aspect")).hasBeenCreated()
			.and(brjs.templateGroup("default").template("aspect-test-unit-default")).hasBeenCreated()
			.and(brjs.templateGroup("default").template("aspect-test-acceptance-default")).hasBeenCreated()
    		.and(testSdkDirectory).containsFileWithContents("sdk/jsdoc-toolkit-resources/jsdoc-placeholders/index.html", "PLACEHOLDER");
		when( brjs.app("app1" ) ).create();
		then(brjs).hasDir("generated/app/app1/jsdoc")
			.and(brjs.app("app1").storageDir("jsdoc")).containsFileWithContents("index.html", "PLACEHOLDER");
	}
	
}
