package org.bladerunnerjs.plugin.appdeployer;

import java.io.IOException;
import java.net.ServerSocket;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.model.events.AppDeployedEvent;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.plugin.Event;
import org.bladerunnerjs.api.plugin.EventObserver;
import org.bladerunnerjs.api.plugin.ModelObserverPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractModelObserverPlugin;
import org.bladerunnerjs.appserver.BRJSApplicationServer;
import org.bladerunnerjs.model.engine.Node;


public class AppDeploymentObserverPlugin extends AbstractModelObserverPlugin implements EventObserver, ModelObserverPlugin
{
	
	// TODO: this message is currently only used by an integration test and not by BRJS command runner
	public class Messages {
		public static final String NEW_APP_LOG_MSG = "New app '%s' found, creating deploy file.";
		public static final String NEW_APP_APPSERVER_NOT_RUNNING_MSG = "New app '%s' found but appserver is not running. Deploy file will not be crated.";
	}
	
	private Logger logger;
	private BRJS brjs;

	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
		brjs.addObserver( AppDeployedEvent.class, this );
		logger = brjs.logger(this.getClass());
	}

	@Override
	public void onEventEmitted(Event event, Node node)
	{
		App app = (App) node;
		
		try
		{
			if (isAppServerRunning()) {
				logger.debug(Messages.NEW_APP_LOG_MSG, app.getName());
				app.file(BRJSApplicationServer.DEPLOY_APP_FILENAME).createNewFile();
			} else {
				logger.debug(Messages.NEW_APP_APPSERVER_NOT_RUNNING_MSG, app.getName());
			}
		}
		catch(Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	private boolean isAppServerRunning() throws ConfigException {
		int appserverPort = brjs.bladerunnerConf().getJettyPort();
		try (ServerSocket socket = new ServerSocket(appserverPort))
		{
			socket.getClass(); /* reference socket to prevent the compiler complaining that is isn't referenced */
			return false;
		}
		catch (IOException ex)
		{
			return true;
		}
	}

}
