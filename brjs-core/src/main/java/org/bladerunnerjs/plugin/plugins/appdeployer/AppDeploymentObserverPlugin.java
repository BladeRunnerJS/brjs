package org.bladerunnerjs.plugin.plugins.appdeployer;

import org.bladerunnerjs.appserver.BRJSApplicationServer;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.events.AppDeployedEvent;
import org.bladerunnerjs.plugin.Event;
import org.bladerunnerjs.plugin.EventObserver;
import org.bladerunnerjs.plugin.ModelObserverPlugin;
import org.bladerunnerjs.plugin.base.AbstractModelObserverPlugin;


public class AppDeploymentObserverPlugin extends AbstractModelObserverPlugin implements EventObserver, ModelObserverPlugin
{
	
	// TODO: this message is currently only used by an integration test and not by BRJS command runner
	public class Messages {
		public static final String NEW_APP_LOG_MSG = "New app '%s' found, creating deploy file.";
	}
	
	private Logger logger;

	@Override
	public void setBRJS(BRJS brjs)
	{
		brjs.addObserver( AppDeployedEvent.class, this );
		logger = brjs.logger(LoggerType.APP_SERVER, this.getClass());
	}

	@Override
	public void onEventEmitted(Event event, Node node)
	{
		App app = (App) node;
		logger.debug(Messages.NEW_APP_LOG_MSG, app.getName());
		
		try
		{
			app.file(BRJSApplicationServer.DEPLOY_APP_FILENAME).createNewFile();
		}
		catch(Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

}
