package org.bladerunnerjs.spec.brjs.appserver;

import static org.mockito.Mockito.mock;

import java.io.IOException;

import javax.naming.Context;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.appserver.filter.TokenisingServletFilter;
import org.bladerunnerjs.appserver.util.JndiTokenFinder;
import org.bladerunnerjs.model.TemplateGroup;
import org.bladerunnerjs.plugin.commands.standard.BuildAppCommand;
import org.bladerunnerjs.utility.ServerUtility;
import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

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
	private Aspect rootAspect;
	private Context mockJndiContext;
	private TemplateGroup templates;
	
	@Before
	public void initTestObjects() throws Exception
	{
		System.setProperty("java.naming.factory.url.pkgs", "org.eclipse.jetty.jndi");
		System.setProperty("java.naming.factory.initial", "org.bladerunnerjs.appserver.filter.TestContextFactory");
		
		given(brjs).hasCommandPlugins(new BuildAppCommand())
			.and(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasTagHandlerPlugins(new MockTagHandler("tagToken", "dev replacement", "prod replacement"))
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			loginAspect = app.aspect("login");
			rootAspect = app.defaultAspect();
			templates = brjs.sdkTemplateGroup("default");
			mockJndiContext = mock(Context.class);
	}
	
	@Test
	public void exportedWarCanBeDeployedOnAnAppServer() throws Exception {
		given(brjs).localeForwarderHasContents("Locale Forwarder")
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
	public void exportedWarCanBeDeployedOnAnAppServerWithRootAspect() throws Exception {
		given(brjs).localeForwarderHasContents("Locale Forwarder")
			.and(rootAspect).containsFileWithContents("index.html", "Hello World!")
			.and(rootAspect).containsResourceFileWithContents("template.html", "<div id='template-id'>content</div>")
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
		given(brjs).localeForwarderHasContents("locale-forwarder.js")
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
		given(brjs).localeForwarderHasContents("locale-forwarder.js")
			.and(aspect).indexPageHasContent("<@js.bundle @/>\n"+"require('appns/Class');")
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
			.and(brjs).localeForwarderHasContents("locale-forwarder.js")
			.and(aspect).indexPageHasContent("<@css.bundle @/>\n")
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
		given(brjs).localeForwarderHasContents("locale-forwarder.js")
			.and(loginAspect).containsFileWithContents("index.html", "Hello World!")
			.and(loginAspect).containsFileWithContents("themes/noir/style.css", ".style { background:url('images/file.gif'); }")
			.and(loginAspect).containsFileWithContents("themes/noir/images/file.gif", "** SOME GIF STUFF... **")
			.and(brjs).hasProdVersion("1234")
			.and(app).hasBeenBuiltAsWar(brjs.dir())
			.and(warServer).hasWar("app1.war", "app")
			.and(warServer).hasStarted();
		when(warServer).receivesRequestFor("/app/login/v/1234/cssresource/aspect_login/theme_noir/images/file.gif", warResponse);
		then(warResponse).textEquals("** SOME GIF STUFF... **");
	}

	@Test
	public void correctContentLengthHeaderIsSetWhenTagsAreReplaced() throws Exception
	{
		given(brjs).localeForwarderHasContents("Locale Forwarder")
    		.and(aspect).containsFileWithContents("index.html", "<@tagToken @/>")
    		.and(brjs).hasProdVersion("1234")
    		.and(app).hasBeenBuiltAsWar(brjs.dir())
    		.and(warServer).hasWar("app1.war", "app")
    		.and(warServer).hasStarted();
    	then(warServer).requestForUrlReturns("/app/en/", "prod replacement")
    		.and(warServer).contentLengthForRequestIs("/app/en/", "prod replacement".getBytes().length);	
	}
	
	@Test
	public void jndiTokensAreReplaced() throws Exception
	{
		given(brjs).localeForwarderHasContents("locale-forwarder.js")
			.and(templates).templateGroupCreated()
			.and(templates.template("app")).containsFile("fileForApp.txt")
			.and(app).hasBeenPopulated("default")
			.and(aspect).containsFileWithContents("index.html", "@SOME.TOKEN@")
			.and(brjs).hasProdVersion("1234")
    		.and(app).hasBeenBuiltAsWar(brjs.dir())
    		.and(warServer).hasWarWithFilters("app1.war", "app", new TokenisingServletFilter(new JndiTokenFinder(mockJndiContext)))
    		.and(warServer).hasStarted();
			Mockito.when(mockJndiContext.lookup("java:comp/env/SOME.TOKEN")).thenReturn("some token replacement");
		then(warServer).requestForUrlReturns("/app/en/", "some token replacement");
	}
	
	@Test
	public void correctContentLengthIsSetWhenJNDITokensAreReplaced() throws Exception
	{
		given(brjs).localeForwarderHasContents("locale-forwarder.js")
			.and(templates).templateGroupCreated()
			.and(templates.template("app")).containsFile("fileForApp.txt")
			.and(app).hasBeenPopulated("default")
    		.and(aspect).containsFileWithContents("index.html", "@SOME.TOKEN@")
    		.and(brjs).hasProdVersion("1234")
    		.and(app).hasBeenBuiltAsWar(brjs.dir())
    		.and(warServer).hasWarWithFilters("app1.war", "app", new TokenisingServletFilter(new JndiTokenFinder(mockJndiContext)))
    		.and(warServer).hasStarted();
			Mockito.when(mockJndiContext.lookup("java:comp/env/SOME.TOKEN")).thenReturn("some token replacement");
    	then(warServer).requestForUrlReturns("/app/en/", "some token replacement")
    		.and(warServer).contentLengthForRequestIs("/app/en/", "some token replacement".getBytes().length);
	}
	
	@Test
	public void correctContentLengthIsSetWhenJNDITokensAreReplacedAndADownstreamFilterCommitsTheResponseEarly() throws Exception
	{
		given(brjs).localeForwarderHasContents("locale-forwarder.js")
			.and(templates).templateGroupCreated()
			.and(templates.template("app")).containsFile("fileForApp.txt")
			.and(app).hasBeenPopulated("default")
    		.and(aspect).containsFileWithContents("index.html", "@SOME.TOKEN@")
    		.and(brjs).hasProdVersion("1234")
    		.and(app).hasBeenBuiltAsWar(brjs.dir())
    		.and(warServer).hasWarWithFilters("app1.war", "app", new TokenisingServletFilter(new JndiTokenFinder(mockJndiContext)), new MockCommitResponseFilter())
    		.and(warServer).hasStarted();
			Mockito.when(mockJndiContext.lookup("java:comp/env/SOME.TOKEN")).thenReturn("some token replacement");
    	then(warServer).requestForUrlReturns("/app/en/", "some token replacement")
    		.and(warServer).contentLengthForRequestIs("/app/en/", "some token replacement".getBytes().length);
	}
	
	@Ignore //TODO: why cant we use real JNDI (a non mock naming context) in tests?
	@Test
	public void jndiTokensCanBeReplacedInAStandaloneWebserverWithoutAMockNamingContext() throws Exception {
		System.setProperty("java.naming.factory.url.pkgs", "org.eclipse.jetty.jndi");
		System.setProperty("java.naming.factory.initial", "org.eclipse.jetty.jndi.InitialContextFactory");
		System.setProperty("org.apache.jasper.compiler.disablejsr199","true");
		
		given(brjs).localeForwarderHasContents("locale-forwarder.js")
    		.and(app).hasBeenPopulated("default")
    		.and(aspect).containsFileWithContents("index.html", "@SOME.TOKEN@")
    		.and(brjs).hasProdVersion("1234")
    		.and(app).hasBeenBuiltAsWar(brjs.dir());
		
		// taken from http://www.eclipse.org/jetty/documentation/current/jndi-embedded.html
		warServer = new Server(appServerPort);
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setWar(brjs.workingDir().file("app1.war").getAbsolutePath());
        warServer.setHandler(webapp);
        new EnvEntry(warServer, "SOME.TOKEN", "some token replacement", false);
 
        given(warServer).hasStarted();
        then(warServer).requestForUrlReturns("/en/", "some token replacement");
	}
	
	
	
	
	private class MockCommitResponseFilter implements Filter {
		@Override
		public void init(FilterConfig filterConfig) throws ServletException
		{
		}
		@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
		{
			response.flushBuffer();
			chain.doFilter(request, response);
		}
		@Override
		public void destroy()
		{			
		}
	}
	
}