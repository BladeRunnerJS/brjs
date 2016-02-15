package org.bladerunnerjs.spec.app;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.AppConf;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.api.BladeWorkbench;
import org.bladerunnerjs.spec.brjs.appserver.MockTagHandler;
import org.bladerunnerjs.testing.utility.MockContentPlugin;
import org.junit.Before;
import org.junit.Test;

public class ServeAppTest extends SpecTest {
	private App app;
	private AppConf appConf;
	private Aspect defaultAspect;
	private Aspect alternateAspect;
	private BladeWorkbench workbench;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsAssetPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).automaticallyFindsContentPlugins()
			.and(brjs).automaticallyFindsRequirePlugins()
			.and(brjs).hasTagHandlerPlugins(new MockTagHandler("tagToken", "dev replacement", "prod replacement", false), new MockTagHandler("localeToken", "", "", true))
			.and(brjs).hasContentPlugins(new MockContentPlugin())
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			appConf = app.appConf();
			defaultAspect = app.aspect("default");
			alternateAspect = app.aspect("alternate");
			workbench = app.bladeset("bs").blade("b1").workbench();
	}
	
	@Test
	public void indexPageCanBeAccessedForSingleLocaleApps() throws Exception {
		given(defaultAspect).indexPageHasContent("index page")
			.and(brjs).localeSwitcherHasContents("");
		when(app).requestReceived("", response);
		then(response).textEquals("index page");
	}
	
	@Test
	public void localeForwardingPageIsReturnedIfNoLocaleIsSpecifiedForMultiLocaleApps() throws Exception {
		given(appConf).supportsLocales("en", "de")
			.and(defaultAspect).indexPageHasContent("index page")
			.and(brjs).localeSwitcherHasContents("locale forwarding page");
		when(app).requestReceived("", response);
		then(response).containsText("locale forwarding page");
	}
	
	@Test
	public void localeForwardingPageHasADocType() throws Exception {
		given(appConf).supportsLocales("en", "de")
		.and(defaultAspect).indexPageHasContent("index page")
		.and(brjs).localeSwitcherHasContents("locale forwarding page");
		when(app).requestReceived("", response);
		then(response).containsText("<!DOCTYPE html>");
	}
	
	@Test
	public void localeForwardingPageHasANoScriptOptionToRedirectToDefaultLocale() throws Exception {
		given(appConf).supportsLocales("en", "de")
			.and(defaultAspect).indexPageHasContent("index page")
			.and(brjs).localeSwitcherHasContents("locale forwarding page");
		when(app).requestReceived("", response);
		then(response).containsText("<noscript>\n"+"\t<meta http-equiv='refresh' content='0; url='en'>\n"+"</noscript>");
	}
	
	@Test
	public void exceptionIsThrownIfAnInvalidLocaleIsRequestedForMultiLocaleApps() throws Exception {
		given(appConf).supportsLocales("en", "de")
			.and(defaultAspect).indexPageHasContent("index page")
    		.and(brjs).localeSwitcherHasContents("locale forwarding page");
    	when(app).requestReceived("zz", response);
    	then(exceptions).verifyException(ResourceNotFoundException.class, "zz");
	}
	
	@Test
	public void indexPageCanBeAccessedForMultiLocaleApps() throws Exception {
		given(appConf).supportsLocales("en", "de")
			.and(defaultAspect).indexPageHasContent("index page")
			.and(brjs).localeSwitcherHasContents("");
		when(app).requestReceived("en", response);
		then(response).textEquals("index page");
	}
	
	@Test
	public void tagsWithinIndexPagesAreProcessed() throws Exception {
		given(defaultAspect).indexPageHasContent("<@tagToken @/>")
			.and(brjs).localeSwitcherHasContents("");
		when(app).requestReceived("", response);
		then(response).textEquals("dev replacement");
	}
	
	@Test
	public void localesCanBeUsedInTagHandlersInSingleLocaleApps() throws Exception {
		given(appConf).supportsLocales("en_GB")
			.and(defaultAspect).indexPageHasContent("<@localeToken @/>")
			.and(brjs).localeSwitcherHasContents("");
		when(app).requestReceived("", response);
		then(response).textEquals("- en_GB");
	}
	
	@Test
	public void localesCanBeUsedInTagHandlersInMultiLocaleApps() throws Exception {
		given(appConf).supportsLocales("en", "en_GB")
			.and(defaultAspect).indexPageHasContent("<@localeToken @/>")
			.and(brjs).localeSwitcherHasContents("");
		when(app).requestReceived("en_GB", response);
		then(response).textEquals("- en_GB");
	}
	
	@Test
	public void workbenchPageCanBeAccessedInSingleLocaleApps() throws Exception {
		given(workbench).indexPageHasContent("workbench index page")
			.and(brjs).localeSwitcherHasContents("");
		when(app).requestReceived("bs/b1/workbench/", response);
		then(response).textEquals("workbench index page");
	}
	
	@Test
	public void workbenchPageCanBeAccessedInMultiLocaleApps() throws Exception {
		given(appConf).supportsLocales("en", "en_GB")
			.and(workbench).indexPageHasContent("workbench index page")
			.and(brjs).localeSwitcherHasContents("");
		when(app).requestReceived("bs/b1/workbench/en", response);
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
		when(app).requestReceived("bs/b1/workbench/v/dev/html/bundle.html", response);
		then(response).containsText("workbench template file");
	}
	
	@Test
	public void contentPluginsCanDefineNonVersionedUrls() throws Exception
	{
		given(app).hasBeenCreated();
		when(app).requestReceived("mock-content-plugin/unversioned/url", response);
		then(response).containsText(MockContentPlugin.class.getCanonicalName());
	}
}
