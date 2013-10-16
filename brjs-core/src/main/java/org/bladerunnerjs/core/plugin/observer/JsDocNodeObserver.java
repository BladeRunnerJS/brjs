package org.bladerunnerjs.core.plugin.observer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerType;
import org.bladerunnerjs.core.plugin.Event;
import org.bladerunnerjs.core.plugin.EventObserver;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.events.NodeReadyEvent;


public class JsDocNodeObserver implements EventObserver
{
	public class Messages {
		public static final String CREATED_JSDOC_FOR_APP_LOG_MSG = "Placeholder jsdoc generated for app '%s'.";
		public static final String NOT_CREATING_JSDOC_FOR_ALREADY_POPULATED_APP_LOG_MSG = "Placeholder jsdoc not being generated for app '%s' as it's already been done before.";
		public static final String IO_ERROR_WHILE_WRITING_PLACEHOLDER_DOCS_LOG_MSG = "IO error while writing placeholder jsdoc for app '%s'";
	}
	
	private final Logger logger;
	private final List<String> fileNames = Arrays.asList(new String[] {"generated-docs.html", "index.html", "swiffy.js"});
	
	public JsDocNodeObserver(BRJS brjs) {
		logger = brjs.logger(LoggerType.OBSERVER, this.getClass());
	}
	
	@Override
	public void onEventEmitted(Event event, Node node) {
		if (event instanceof NodeReadyEvent) {
			createJsDocPlaceHolder(node);
		}
	}
	
	private void createJsDocPlaceHolder(Node node) {
		if(node instanceof App) {
			App app = (App) node;
			
			try {
				if(app.storageFile("jsdoc-toolkit", "index.html").exists()) {
					logger.debug(Messages.NOT_CREATING_JSDOC_FOR_ALREADY_POPULATED_APP_LOG_MSG, app.getName());
				}
				else {
					for(String fileName : fileNames) {
						File outFile = app.storageFile("jsdoc-toolkit", fileName);
						outFile.getParentFile().mkdirs();
						outFile.createNewFile();
						
						try (InputStream in = getClass().getClassLoader().getResourceAsStream("org/bladerunnerjs/core/plugin/observer/" + fileName);
							OutputStream out = new FileOutputStream(outFile)) {
							IOUtils.copy(in, out);
						}
					}
					
					logger.info(Messages.CREATED_JSDOC_FOR_APP_LOG_MSG, app.getName());
				}
			}
			catch (IOException e) {
				logger.error(Messages.IO_ERROR_WHILE_WRITING_PLACEHOLDER_DOCS_LOG_MSG, app.getName());
			}
		}
	}

}
