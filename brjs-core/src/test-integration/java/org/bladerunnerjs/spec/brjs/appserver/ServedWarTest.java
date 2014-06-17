package org.bladerunnerjs.spec.brjs.appserver;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.plugin.plugins.commands.standard.BuildAppCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.utility.ServerUtility;
import org.eclipse.jetty.server.Server;
import org.junit.Before;
import org.junit.Test;

public class ServedWarTest extends SpecTest {
	private App app;
	private Server warServer = new Server(ServerUtility.getTestPort());
	private StringBuffer forwarderPageResponse = new StringBuffer();
	private StringBuffer pageResponse = new StringBuffer();
	private StringBuffer bundleResponse = new StringBuffer();
	private StringBuffer warResponse = new StringBuffer();
	private StringBuffer brjsResponse = new StringBuffer();
	private Aspect aspect;
	private Aspect loginAspect;
	private DirNode sdkLibsDir;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommandPlugins(new BuildAppCommand())
			.and(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			loginAspect = app.aspect("login");
			sdkLibsDir = brjs.sdkLibsDir();
	}
	
	@Test
	public void exportedWarCanBeDeployedOnAnAppServer() throws Exception {
		given(sdkLibsDir).containsFileWithContents("locale-forwarder.js", "Locale Forwarder")
			.and(aspect).containsFileWithContents("index.html", "Hello World!")
			.and(aspect).containsResourceFileWithContents("template.html", "<div id='template-id'>content</div>")
			.and(brjs).hasProdVersion("1234")
			.and(app).hasBeenBuiltAsWar(brjs.dir())
			.and(warServer).hasWar("app1.war", "app")
			.and(warServer).hasStarted();
		when(warServer).receivesRequestFor("/app", forwarderPageResponse)
			.and(warServer).receivesRequestFor("/app/en", pageResponse)
			.and(warServer).receivesRequestFor("/app/v/1234/html/bundle.html", bundleResponse);
		then(forwarderPageResponse).containsText("Locale Forwarder")
			.and(pageResponse).containsText("Hello World!")
			.and(bundleResponse).isNotEmpty();
	}
	
	@Test
	public void exportedWarIndexPageIsTheSameAsBrjsHosted() throws Exception {
		given(sdkLibsDir).containsFile("locale-forwarder.js")
			.and(aspect).containsFileWithContents("index.html", "Hello World!")
			.and(app).hasBeenBuiltAsWar(brjs.dir())
			.and(warServer).hasWar("app1.war", "app")
			.and(warServer).hasStarted();
		when(warServer).receivesRequestFor("/app/en", warResponse)
			.and(app).requestReceived("en/", brjsResponse);
		then(warResponse).textEquals(brjsResponse);
	}
	
	@Test
	public void exportedWarJsBundleIsTheSameAsBrjsHosted() throws Exception {
		given(sdkLibsDir).containsFile("locale-forwarder.js")
			.and(aspect).indexPageRequires("appns/Class")
			.and(aspect).hasClass("appns/Class")
			.and(brjs).hasProdVersion("APP.VERSION")
			.and(brjs).hasDevVersion("APP.VERSION")
			.and(app).hasBeenBuiltAsWar(brjs.dir())
			.and(warServer).hasWar("app1.war", "app")
			.and(warServer).hasStarted();
		when(warServer).receivesRequestFor("/app/v/APP.VERSION/js/prod/combined/bundle.js", warResponse)
			.and(app).requestReceived("v/APP.VERSION/js/prod/combined/bundle.js", brjsResponse);
		then(warResponse).textEquals(brjsResponse);
	}
	
	@Test
	public void exportedWarCssBundleIsTheSameAsBrjsHosted() throws Exception {
		given(aspect).containsResourceFileWithContents("style.css", "body { color: red; }")
			.and(sdkLibsDir).containsFile("locale-forwarder.js")
			.and(aspect).containsFileWithContents("index.html", "Hello World!")
			.and(brjs).hasProdVersion("1234")
			.and(app).hasBeenBuiltAsWar(brjs.dir())
			.and(warServer).hasWar("app1.war", "app")
			.and(warServer).hasStarted();
		when(warServer).receivesRequestFor("/app/v/1234/css/common/bundle.css", warResponse)
			.and(app).requestReceived("v/1234/css/common/bundle.css", brjsResponse);
		then(warResponse).textEquals(brjsResponse);
	}
	
	@Test
	public void warCommandDoesntExportFilesFromAnotherAspect() throws Exception {
		given(sdkLibsDir).containsFile("locale-forwarder.js")
			.and(loginAspect).containsFileWithContents("index.html", "Hello World!")
			.and(loginAspect).containsFileWithContents("themes/noir/images/file.gif", "** SOME GIF STUFF... **")
			.and(brjs).hasProdVersion("1234")
			.and(app).hasBeenBuiltAsWar(brjs.dir())
			.and(warServer).hasWar("app1.war", "app")
			.and(warServer).hasStarted();
		when(warServer).receivesRequestFor("/app/login/v/1234/cssresource/aspect_login/theme_noir/images/file.gif", warResponse);
		then(warResponse).textEquals("** SOME GIF STUFF... **");
	}
	
}