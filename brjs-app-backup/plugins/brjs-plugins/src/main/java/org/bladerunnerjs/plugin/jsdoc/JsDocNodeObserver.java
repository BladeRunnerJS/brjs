package org.bladerunnerjs.plugin.jsdoc;

import java.io.IOException;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.model.events.NodeDiscoveredEvent;
import org.bladerunnerjs.api.model.events.NodeReadyEvent;
import org.bladerunnerjs.api.plugin.Event;
import org.bladerunnerjs.api.plugin.EventObserver;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.plugin.commands.standard.JsDocCommand;


public class JsDocNodeObserver implements EventObserver
{
	public class Messages {
		public static final String CREATED_JSDOC_FOR_APP_LOG_MSG = "Placeholder jsdoc generated for app '%s'.";
		public static final String NOT_CREATING_JSDOC_FOR_ALREADY_POPULATED_APP_LOG_MSG = "Placeholder jsdoc not being generated for app '%s' as it's already been done before.";
		public static final String IO_ERROR_WHILE_WRITING_PLACEHOLDER_DOCS_LOG_MSG = "IO error while writing placeholder jsdoc for app '%s'";
	}
	
	private final Logger logger;
	
	public JsDocNodeObserver(BRJS brjs) {
		logger = brjs.logger(this.getClass());
	}
	
	@Override
	public void onEventEmitted(Event event, Node node) {
		if (event instanceof NodeReadyEvent || event instanceof NodeDiscoveredEvent) {
			createJsDocPlaceHolder(node);
		}
	}
	
	private void createJsDocPlaceHolder(Node node) {
		if (node instanceof App) {
			App app = (App) node;
			
			try {
				JsDocCommand.copyJsDocPlaceholder( app );
			}
			catch (IOException e) {
				logger.error(Messages.IO_ERROR_WHILE_WRITING_PLACEHOLDER_DOCS_LOG_MSG, app.getName());
			}
		}
	}

}
