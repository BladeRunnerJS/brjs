package org.bladerunnerjs.spec.app;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.spec.brjs.appserver.MockTagHandler;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AppServeTest extends SpecTest {
	private App app;
	private Aspect defaultAspect;
	private Aspect alternateAspect;
	private Workbench workbench;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsAssetLocationProducers()
			.and(brjs).automaticallyFindsAssetProducers()
			.and(brjs).automaticallyFindsContentPlugins()
			.and(brjs).hasTagPlugins(new MockTagHandler("tagToken", "dev replacement", "prod replacement", false), new MockTagHandler("localeToken", "", "", true))
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			defaultAspect = app.aspect("default");
			alternateAspect = app.aspect("alternate");
			workbench = app.bladeset("bs").blade("b1").workbench();
	}
	
	@Test
	public void localeForwardingPageIsReturnedIfNoLocaleIsSpecified() throws Exception {
		given(defaultAspect).indexPageHasContent("index page")
			.and(brjs).localeForwarderHasContents("locale forwarding page");
		when(app).requestReceived("", response);
		then(response).containsText("locale forwarding page");
	}
	
	@Test
	public void indexPageCanBeAccessed() throws Exception {
		given(defaultAspect).indexPageHasContent("index page")
			.and(brjs).localeForwarderHasContents("");
		when(app).requestReceived("en/", response);
		then(response).textEquals("index page");
	}
	
	@Test
	public void tagsWithinIndexPagesAreProcessed() throws Exception {
		given(defaultAspect).indexPageHasContent("<@tagToken @/>")
			.and(brjs).localeForwarderHasContents("");
		when(app).requestReceived("en/", response);
		then(response).textEquals("dev replacement");
	}
	
	@Test
	public void localesCanBeUsedInTagHandlers() throws Exception {
		given(defaultAspect).indexPageHasContent("<@localeToken @/>")
			.and(brjs).localeForwarderHasContents("");
		when(app).requestReceived("en_GB/", response);
		then(response).textEquals("- en_GB");
	}
	
	@Test
	public void workbenchPageCanBeAccessed() throws Exception {
		given(workbench).indexPageHasContent("workbench index page")
			.and(brjs).localeForwarderHasContents("");
		when(app).requestReceived("workbench/bs/b1/en/", response);
		then(response).textEquals("workbench index page");
	}
	
	@Test
	public void defaultAspectBundlesCanBeRequested() throws Exception {
		given(defaultAspect).indexPageRequires("appns/SomeClass")
			.and(defaultAspect).hasClass("appns/SomeClass")
			.and(defaultAspect).containsFileWithContents("src/appns/template.html", "<div id='template-id'>template file</div>");
		when(app).requestReceived("v/dev/html/bundle.html", response);
		then(response).containsText("template file");
	}
	
	@Test
	public void alternateAspectBundlesCanBeRequested() throws Exception {
		given(alternateAspect).indexPageRequires("appns/SomeClass")
			.and(alternateAspect).hasClass("appns/SomeClass")
			.and(alternateAspect).containsFileWithContents("src/appns/template.html", "<div id='template-id'>template file</div>");
		when(app).requestReceived("alternate/v/dev/html/bundle.html", response);
		then(response).containsText("template file");
	}
	
	@Test
	public void workbenchBundlesCanBeRequested() throws Exception {
		given(workbench).indexPageRequires("appns/SomeClass")
			.and(workbench).hasClass("appns/SomeClass")
			.and(workbench).containsFileWithContents("src/appns/template.html", "<div id='template-id'>workbench template file</div>");
		when(app).requestReceived("workbench/bs/b1/v/dev/html/bundle.html", response);
		then(response).containsText("workbench template file");
	}
}
