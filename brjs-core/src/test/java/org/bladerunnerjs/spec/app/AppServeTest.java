package org.bladerunnerjs.spec.app;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AppServeTest extends SpecTest {
	private App app;
	private Aspect defaultAspect;
	private Aspect alternateAspect;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsAssetLocationProducers()
			.and(brjs).automaticallyFindsAssetProducers()
			.and(brjs).automaticallyFindsContentPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			defaultAspect = app.aspect("default");
			alternateAspect = app.aspect("alternate");
	}
	
	// TODO: we should create a full suite of logical app handling tests here, then pare down ServedAppTest and ServedAppBundleTest to only contain tests that need to go through a proper app server
	
	@Test
	public void defaultAspectBundlesCanBeRequested() throws Exception {
		given(defaultAspect).indexPageRequires("appns/SomeClass")
			.and(defaultAspect).hasClass("appns/SomeClass")
			.and(defaultAspect).containsFileWithContents("src/appns/template.html", "<div id='template-id'>template file</div>");
		when(app).requestReceived("v/dev/bundle.html", response);
		then(response).containsText("template file");
	}
	
	@Test
	public void alternateAspectBundlesCanBeRequested() throws Exception {
		given(alternateAspect).indexPageRequires("appns/SomeClass")
			.and(alternateAspect).hasClass("appns/SomeClass")
			.and(alternateAspect).containsFileWithContents("src/appns/template.html", "<div id='template-id'>template file</div>");
		when(app).requestReceived("alternate/v/dev/bundle.html", response);
		then(response).containsText("template file");
	}
}
