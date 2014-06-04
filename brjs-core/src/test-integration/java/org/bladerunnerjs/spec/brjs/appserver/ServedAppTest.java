package org.bladerunnerjs.spec.brjs.appserver;


import java.net.ServerSocket;

import org.bladerunnerjs.appserver.ApplicationServer;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class ServedAppTest extends SpecTest
{
	ApplicationServer appServer;
	App app;
	Aspect aspect;
	Blade blade;
	Workbench workbench;
	DirNode appJars;
	ServerSocket socket;
	StringBuffer response = new StringBuffer();
	DirNode sdkLibsDir;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsMinifiers()
			.and(brjs).automaticallyFindsAssetLocationProducers()
			.and(brjs).automaticallyFindsAssetProducers()
			.and(brjs).hasTagPlugins( new MockTagHandler("tagToken", "dev replacement", "prod replacement", false), new MockTagHandler("localeToken", "", "", true) )
			.and(brjs).hasContentPlugins(new MockContentPlugin())
			.and(brjs).hasBeenCreated()
			.and(brjs).usedForServletModel()
			.and(brjs).containsFolder("apps")
			.and(brjs).containsFolder("sdk/system-applications")
			.and(brjs).usesProductionTemplates();
			appServer = brjs.applicationServer(appServerPort);
			app = brjs.app("app");
			aspect = app.aspect("default");
			blade = app.bladeset("bs").blade("b1");
			workbench = blade.workbench();
			appJars = brjs.appJars();
			appJars.create();
			sdkLibsDir = brjs.sdkLibsDir();
	}
	
	@After
	public void stopServer() throws Exception
	{
		given(brjs.applicationServer(appServerPort)).stopped()
			.and(brjs.applicationServer(appServerPort)).requestTimesOutFor("/");
		if (socket  != null && socket.isBound()) { socket.close(); }
	}
	
	@Test
	public void localeForwardingPageIsReturnedIfNoLocaleIsSpecified() throws Exception {
		given(app).hasBeenPopulated()
			.and(sdkLibsDir).containsFileWithContents("locale-forwarder.js", "locale forwarding page")
			.and(appServer).started();
		then(appServer).requestForUrlContains("/app/", "locale forwarding page")
			.and(appServer).requestForUrlContains("/app/", "<noscript><meta http-equiv='refresh' content='0; url=en/'></noscript>");
	}
	
	@Test
	public void jspSupportIsEnabled() throws Exception
	{
		given(app).hasBeenPopulated()
			.and(aspect).containsFileWithContents("index.jsp", "Hello world!")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/default-aspect/index.jsp", "Hello world!");
	}
	
	@Test
	public void indexPageCanBeAccessed() throws Exception
	{
		given(app).hasBeenPopulated()
			.and(aspect).containsFileWithContents("index.html", "aspect index.html")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/en/", "aspect index.html");
	}
	
	@Test
	public void jspIndexPageCanBeAccessed() throws Exception
	{
		given(app).hasBeenPopulated()
			.and(aspect).containsFileWithContents("index.jsp", "<%= \"aspect \" + \"index.jsp\" %>")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/en/", "aspect index.jsp");
	}
	
	@Test
	public void tagsWithinIndexPagesAreProcessed() throws Exception
	{
		given(app).hasBeenPopulated()
			.and(aspect).containsFileWithContents("index.html", "<@tagToken @/>")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/en/", "dev replacement");
	}
	
	@Test
	public void localesCanBeUsedInTagHandlers() throws Exception
	{
		given(app).hasBeenPopulated()
			.and(app).hasSupportedLocales("ab_CD")
			.and(aspect).containsFileWithContents("index.html", "<@localeToken @/>")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/ab_CD/", "- ab_CD");
	}
	
	@Test
	public void workbenchPageCanBeAccessed() throws Exception {
		given(app).hasBeenPopulated()
			.and(workbench).containsFileWithContents("index.html", "workbench index.html")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/workbench/bs/b1/en/", "workbench index.html");
	}
	
	@Test
	public void contentPluginsCanHandleRequests() throws Exception
	{
		given(app).hasBeenPopulated()
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/v/123/mock-content-plugin/", MockContentPlugin.class.getCanonicalName());
	}
	
	@Test
	public void contentPluginsCanHandleRequestsWithinWorkbenches() throws Exception {
		given(app).hasBeenPopulated()
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/workbench/bs/b1/v/123/mock-content-plugin/", MockContentPlugin.class.getCanonicalName());
	}
	
	@Test @Ignore
	public void bladeRunnerJSDoesntBreakAuthentication() {
		// TODO
	}
	
	@Test
	public void longUrlsDontGetHandedToOtherServlets() throws Exception
	{
		given(app).hasBeenPopulated()
			.and(appServer).started()
			.and(appServer).appHasServlet(app, new HelloWorldServlet(), "/servlet/hello");
		then(appServer).requestForUrlReturns("/app/v/123/mock-content-plugin/some/other/path/", MockContentPlugin.class.getCanonicalName())
			.and(appServer).requestForUrlReturns("/app/servlet/hello", "Hello World!");
	}
	
}
