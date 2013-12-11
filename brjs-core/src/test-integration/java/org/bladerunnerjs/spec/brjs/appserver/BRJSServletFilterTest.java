package org.bladerunnerjs.spec.brjs.appserver;

import java.net.ServerSocket;

import javax.servlet.Servlet;

import org.bladerunnerjs.appserver.ApplicationServer;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.utility.MockTagHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class BRJSServletFilterTest extends SpecTest
{

	ApplicationServer appServer;
	App app;
	Aspect aspect;
	Blade blade;
	DirNode appJars;
	ServerSocket socket;
	Servlet helloWorldServlet;
	StringBuffer response = new StringBuffer();

	@Before
	public void initTestObjects() throws Exception {
		
		given(brjs).hasTagPlugins( new MockTagHandler("tagToken", "dev replacement", "prod replacement", false), new MockTagHandler("localeToken", "", "", true) )
			.and(brjs).automaticallyFindsAssetProducers()
			.and(brjs).hasBeenCreated()
			.and(brjs).usedForServletModel();
    		appServer = brjs.applicationServer(appServerPort);
    		app = brjs.app("app");
    		aspect = app.aspect("default");
    		blade = app.bladeset("bs").blade("b1");
    		appJars = brjs.appJars();
    		appJars.create();
    		helloWorldServlet = new HelloWorldServlet();
	}
	
	
	@After
	public void stopServer() throws Exception
	{
		given(brjs.applicationServer(appServerPort)).stopped()
			.and(brjs.applicationServer(appServerPort)).requestTimesOutFor("/");
		if (socket  != null && socket.isBound()) { socket.close(); }
	}
	
	@Test
	public void indexFilesWithoutTagsAreUnchanged() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsFileWithContents("index.html", "some html content")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/default-aspect/index.html", "some html content");
	}
	
	@Test
	public void aspectIndexFileIsFiltered() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(aspect).hasBeenCreated()
    		.and(aspect).containsFileWithContents("index.html", "<@tagToken @/>")
    		.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/default-aspect/index.html", "dev replacement");
	}
	
	@Test
	public void localesCanBeUsedInTagHandlers() throws Exception
	{
		given(app).hasBeenCreated()
			.and(app).hasSupportedLocales("ab_CD")
    		.and(aspect).hasBeenCreated()
    		.and(aspect).containsFileWithContents("index.html", "<@localeToken @/>")
    		.and(appServer).started();
		when(webappTester).makesRequestWithLocale("ab_CD");
		then(appServer).requestForUrlReturns("/app/default-aspect/index.html", "- ab_CD");
	}
	
	//TODO: test for bundle set usage
}
