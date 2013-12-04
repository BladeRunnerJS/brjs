package org.bladerunnerjs.model.appserver;

import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerType;
import org.bladerunnerjs.core.plugin.Event;
import org.bladerunnerjs.core.plugin.EventObserver;
import org.bladerunnerjs.core.plugin.ModelObserverPlugin;
import org.bladerunnerjs.core.plugin.observer.AbstractModelObserverPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.events.AppDeployedEvent;


public class AppDeploymentObserver extends AbstractModelObserverPlugin implements EventObserver, ModelObserverPlugin
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
