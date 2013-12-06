package org.bladerunnerjs.model.utility;

import java.io.File;
import java.io.OutputStream;

import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerType;
import org.bladerunnerjs.core.plugin.content.ContentPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;


public class LogicalRequestHandler {
	// TODO: these messages need to be covered off in a spec test (a single test would be perfect)
	public class Messages {
		public static final String REQUEST_HANDLED_MSG = "Handling logical request '%s' for app '%s'.";
		public static final String CONTEXT_IDENTIFIED_MSG = "%s '%s' identified as context for request '%s'.";
		public static final String BUNDLER_IDENTIFIED_MSG = "Bundler '%s' identified as handler for request '%s'.";
	}
	
	private final App app;
	private final Logger logger;
	
	public LogicalRequestHandler(App app) {
		this.app = app;
		logger = app.root().logger(LoggerType.BUNDLER, getClass());
	}
	
	public void handle(BladerunnerUri requestUri, OutputStream os) throws MalformedRequestException, ResourceNotFoundException, BundlerProcessingException {
		logger.debug(Messages.REQUEST_HANDLED_MSG, requestUri.logicalPath, app.getName());
		
		try {
			File baseDir = new File(app.dir(), requestUri.scopePath);
			BundlableNode bundlableNode = app.root().locateFirstBundlableAncestorNode(baseDir);
			
			if(bundlableNode == null) {
				throw new ResourceNotFoundException("No bundlable resource could be found above the directory '" + baseDir.getPath() + "'");
			}
			
			String name = (bundlableNode instanceof NamedNode) ? ((NamedNode) bundlableNode).getName() : "default";
			logger.debug(Messages.CONTEXT_IDENTIFIED_MSG, bundlableNode.getClass().getSimpleName(), name, requestUri.logicalPath);
			
			ContentPlugin contentProvider = app.root().plugins().contentProvider(requestUri);
			
			if(contentProvider == null) {
				throw new ResourceNotFoundException("No content provider could be found found the logical request path '" + requestUri.logicalPath + "'");
			}
			
			logger.debug(Messages.BUNDLER_IDENTIFIED_MSG, contentProvider.getPluginClass().getSimpleName(), requestUri.logicalPath);
			
			ParsedContentPath contentPath = contentProvider.getContentPathParser().parse(requestUri);
			contentProvider.writeContent(contentPath, bundlableNode.getBundleSet(), os);
		}
		catch(ModelOperationException e) {
			throw new BundlerProcessingException(e);
		}
	}
}
