package org.bladerunnerjs.utility;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.plugin.ContentPlugin;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.engine.NamedNode;


public class BundleSetRequestHandler {
	// TODO: these messages need to be covered off in a spec test (a single test would be perfect)
	public class Messages {
		public static final String REQUEST_HANDLED_MSG = "Handling logical request '%s' for app '%s'.";
		public static final String CONTEXT_IDENTIFIED_MSG = "%s '%s' identified as context for request '%s'.";
		public static final String BUNDLER_IDENTIFIED_MSG = "Bundler '%s' identified as handler for request '%s'.";
	}
	
	public static ResponseContent handle(BundleSet bundleSet, String logicalRequestpath, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException {
		BundlableNode bundlableNode = bundleSet.bundlableNode();
		App app = bundlableNode.app();
		Logger logger = app.root().logger(BundleSetRequestHandler.class);
		
		logger.debug(Messages.REQUEST_HANDLED_MSG, logicalRequestpath, app.getName());
		
		String name = (bundlableNode instanceof NamedNode) ? ((NamedNode) bundlableNode).getName() : "default";
		logger.debug(Messages.CONTEXT_IDENTIFIED_MSG, bundlableNode.getTypeName(), name, logicalRequestpath);
		
		ContentPlugin contentProvider = app.root().plugins().contentPluginForLogicalPath(logicalRequestpath);
		
		if(contentProvider == null) {
			throw new ResourceNotFoundException("No content provider could be found found the logical request path '" + logicalRequestpath + "'");
		}
		
		logger.debug(Messages.BUNDLER_IDENTIFIED_MSG, contentProvider.getPluginClass().getSimpleName(), logicalRequestpath);
		
		return contentProvider.handleRequest(logicalRequestpath, bundleSet, contentAccessor, version);
	}
}
