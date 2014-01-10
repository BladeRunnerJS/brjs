package org.bladerunnerjs.appserver;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.ConfigException;
import org.eclipse.jetty.security.Authenticator;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.authentication.FormAuthenticator;
import org.eclipse.jetty.server.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import static org.bladerunnerjs.appserver.ApplicationServerUtils.Messages.*;

public class ApplicationServerUtils
{	
	public class Messages {
		public static final String DEPLOYING_APP_MSG = "Deploying new app to app server '%s'";
	}
	
	static Map<App,WebAppContext> addAppContexts(BRJS brjs, ContextHandlerCollection contexts) throws Exception
	{
		Map<App,WebAppContext> contextMap = new HashMap<App,WebAppContext>(); 
		for (App app : brjs.systemApps())
		{
			contextMap.put(app, addAppContext(app, contexts) );
		}
		for (App app : brjs.apps())
		{
			contextMap.put(app, addAppContext(app, contexts) );
		}
		return contextMap;
	}
	
	static WebAppContext addAppContext(App app, ContextHandlerCollection contexts) throws Exception
	{
		app.root().logger(LoggerType.APP_SERVER, ApplicationServer.class).debug(DEPLOYING_APP_MSG, app.getName());
		WebAppContext appContext = ApplicationServerUtils.createContextForApp(app);
		
		appContext.addFilter(new FilterHolder(new BRJSServletFilter()), "/*", EnumSet.of(DispatcherType.FORWARD,DispatcherType.REQUEST));
		
		contexts.addHandler(appContext);
		appContext.start();
		ApplicationServerUtils.getDeployFileForApp(app).delete();
		
		return appContext;
	}

	static void addRootContext(BRJS brjs, ContextHandlerCollection contexts)
	{
		ContextHandler rootContext = new ContextHandler();
		rootContext.setContextPath("/");
		rootContext.setHandler( new RootContextHandler() );
		contexts.addHandler(rootContext);
	}
	
	static void addAuthRealmToWebServer(BRJS brjs, Server server) throws IOException, ConfigException
	{
		HashLoginService loginService = new HashLoginService();
		loginService.setName( brjs.bladerunnerConf().getLoginRealm() );
		loginService.setConfig( brjs.usersPropertiesConf().getAbsolutePath() );
		server.addBean(loginService);
	}
	
	static WebAppContext createContextForApp(App app)
	{
		WebAppContext webappContext = new WebAppContext();
		webappContext.setConfigurationClasses(new String[] { 
			"org.eclipse.jetty.webapp.WebInfConfiguration", 
			"org.eclipse.jetty.webapp.WebXmlConfiguration",
			"org.eclipse.jetty.webapp.MetaInfConfiguration",
			"org.eclipse.jetty.webapp.FragmentConfiguration",
			"org.eclipse.jetty.plus.webapp.EnvConfiguration",
			"org.eclipse.jetty.plus.webapp.PlusConfiguration",
			"org.eclipse.jetty.webapp.JettyWebXmlConfiguration"});
		
		// webdefault.xml defines BRJS servlet and BRJS filter
		webappContext.setDefaultsDescriptor("org/bladerunnerjs/model/appserver/webdefault.xml");
		
		File webXml = app.file("WEB-INF/web.xml");
		if (webXml.exists())
		{
			webappContext.setDescriptor(webXml.getAbsolutePath());
		}
		
		webappContext.setResourceBase(app.dir().getPath());
		webappContext.setContextPath("/"+app.getName());
		webappContext.setServerClasses(new String[] {"org.slf4j."});
		webappContext.setParentLoaderPriority(false);
		//webappContext.setExtraClasspath(applicationPath+"/MY_CUSTOM_JARS/");
		/* TOOD: add plugin jars to the classpath 
		 * 		- can be done using webappContext.setExtraClasspath(applicationPath+"/MY_CUSTOM_JARS/");
		 *		- more info at http://www.eclipse.org/jetty/documentation/current/jetty-classloading.html 
		 *				and http://download.eclipse.org/jetty/stable-9/apidocs/org/eclipse/jetty/webapp/WebAppContext.html#setExtraClasspath(java.lang.String) 
		 * */
		
		Authenticator authenticator = webappContext.getSecurityHandler().getAuthenticator();
		
		if(authenticator instanceof FormAuthenticator)
		{
			FormAuthenticator formAuthenticator = (FormAuthenticator) authenticator;
			formAuthenticator.setAlwaysSaveUri(true);
		}
		return webappContext;
	}
	
	static File getDeployFileForApp(App app)
	{
		return app.file(BRJSApplicationServer.DEPLOY_APP_FILENAME);
	}

}
