package org.bladerunnerjs.utility;

import java.io.OutputStream;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.plugin.ContentPlugin;


public class BundleSetRequestHandler {
	// TODO: these messages need to be covered off in a spec test (a single test would be perfect)
	public class Messages {
		public static final String REQUEST_HANDLED_MSG = "Handling logical request '%s' for app '%s'.";
		public static final String CONTEXT_IDENTIFIED_MSG = "%s '%s' identified as context for request '%s'.";
		public static final String BUNDLER_IDENTIFIED_MSG = "Bundler '%s' identified as handler for request '%s'.";
	}
	
	public static void handle(BundleSet bundleSet, String logicalRequestpath, OutputStream os, String version) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException {
		BundlableNode bundlableNode = bundleSet.getBundlableNode();
		App app = bundlableNode.app();
		Logger logger = app.root().logger(LoggerType.BUNDLER, BundleSetRequestHandler.class);
		
		logger.debug(Messages.REQUEST_HANDLED_MSG, logicalRequestpath, app.getName());
		
		String name = (bundlableNode instanceof NamedNode) ? ((NamedNode) bundlableNode).getName() : "default";
		logger.debug(Messages.CONTEXT_IDENTIFIED_MSG, bundlableNode.getClass().getSimpleName(), name, logicalRequestpath);
		
		ContentPlugin contentProvider = app.root().plugins().contentProviderForLogicalPath(logicalRequestpath);
		
		if(contentProvider == null) {
			throw new ResourceNotFoundException("No content provider could be found found the logical request path '" + logicalRequestpath + "'");
		}
		
		logger.debug(Messages.BUNDLER_IDENTIFIED_MSG, contentProvider.getPluginClass().getSimpleName(), logicalRequestpath);
		
		ParsedContentPath contentPath = contentProvider.getContentPathParser().parse(logicalRequestpath);
		contentProvider.writeContent(contentPath, bundleSet, os, version);
	}
}
