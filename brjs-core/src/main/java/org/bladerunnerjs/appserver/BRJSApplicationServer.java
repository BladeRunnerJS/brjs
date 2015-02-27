package org.bladerunnerjs.appserver;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Servlet;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.appserver.ApplicationServer;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.utility.ServerUtility;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import static org.bladerunnerjs.appserver.BRJSApplicationServer.Messages.*;

public class BRJSApplicationServer implements ApplicationServer
{
	public static final String DEPLOY_APP_FILENAME = ".deploy";
	
	public class Messages {
		public static final String SERVER_STARTING_LOG_MSG = "%s server starting.";
		public static final String SERVER_STOPPING_LOG_MSG = "%s server stopping.";
		public static final String PORT_ALREADY_BOUND_EXCEPTION_MSG = "Port '%s' is already bound. Either another instance of %s is running or another server is using this port.";
		public static final String SERVER_STARTED_LOG_MESSAGE = "Application server started on port %s.";
		public static final String SERVER_STOPPED_LOG_MESSAGE = "Application server running on port %s stopped.";
		public static final String ERROR_CREATING_DEPLOYMENT_WATCHER = "Error creating app deployment watcher. New apps will not be automatically deployed.";	// TODO This message is not used anywhere
	}
	
	private BRJS brjs;
	private Logger logger;
	private int port;
	private Server server;
	private ContextHandlerCollection contexts;
	private Map<App,WebAppContext> contextMap;
	private AppDeploymentFileWatcher fileWatcher;
	private long fileWatcherInterval = -1;
	
	public BRJSApplicationServer(BRJS brjs, int port)
	{
		this.brjs = brjs;
		this.port = port;
		logger = brjs.logger(ApplicationServer.class);
		
		server = new Server();
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(port);
		server.addConnector(connector);
		
		contexts = new ContextHandlerCollection();
		server.setHandler(contexts);
	}

	@Override
	public void start() throws Exception
	{
		System.setProperty("java.naming.factory.url.pkgs", "org.eclipse.jetty.jndi");
		System.setProperty("java.naming.factory.initial", "org.eclipse.jetty.jndi.InitialContextFactory");
		System.setProperty("org.apache.jasper.compiler.disablejsr199","true");
		
		logger.info(SERVER_STARTING_LOG_MSG, BRJS.PRODUCT_NAME);

		if (ServerUtility.isPortBound(port))
		{
			throw new IOException( String.format(PORT_ALREADY_BOUND_EXCEPTION_MSG, port, BRJS.PRODUCT_NAME) );
		}
		
		if(!brjs.appJars().dirExists()) throw new IllegalStateException( "The directory containing the app jars, located at '" + brjs.appJars().dir().getPath() + "', is not present");
		
		ApplicationServerUtils.addAuthRealmToWebServer(brjs, server);
		ApplicationServerUtils.addRootContext(brjs, contexts);
		contextMap = ApplicationServerUtils.addAppContexts(brjs, contexts);
		
		MemoizedFile appsDir = brjs.getMemoizedFile(brjs.dir(), "brjs-apps"); //TODO: this needs to change to current working dir once we have a global install
		MemoizedFile sysAppsDir = brjs.systemApp("no-such-app").dir().getParentFile();
		fileWatcher = new AppDeploymentFileWatcher(brjs, this, fileWatcherInterval, appsDir, sysAppsDir);
		
		fileWatcher.start();
		server.start();
		logger.info(SERVER_STARTED_LOG_MESSAGE, getPort());
	}

	@Override
	public void stop() throws Exception
	{
		if(fileWatcher != null) {
			fileWatcher.terminate();
			server.stop();
			logger.info(SERVER_STOPPED_LOG_MESSAGE, getPort());
		}
	}

	@Override
	public int getPort()
	{
		return port;
	}

	public synchronized void deployApp(App app) throws Exception
	{
		contextMap.put(app, ApplicationServerUtils.addAppContext(app, contexts) );
	}

	/**
	 * This method should only be used for testing. Allows another servlet to be added to an app.
	 */
	public void addServlet(App app, Servlet servlet, String servletPath) throws Exception
	{
		WebAppContext appContext = contextMap.get(app);
		if (appContext == null)
		{
			throw new RuntimeException("No app context found for app " + app.getName());
		}
		ServletHolder servletHolder = new ServletHolder(servlet);
		appContext.addServlet(servletHolder, servletPath);
		servletHolder.start();
	}
	
	public void setAppDeploymentWatcherInterval(long interval) {
		this.fileWatcherInterval = interval;
	}

	@Override
	public void join() throws Exception
	{
		if (server != null)
		{
			server.join();
		}
	}
	
}
