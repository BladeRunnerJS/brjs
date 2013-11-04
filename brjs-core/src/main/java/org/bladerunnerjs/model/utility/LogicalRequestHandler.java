package org.bladerunnerjs.model.utility;

import java.io.File;
import java.io.OutputStream;

import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;


public class LogicalRequestHandler {
	private App app;
	
	public LogicalRequestHandler(App app) {
		this.app = app;
	}
	
	public void handle(BladerunnerUri requestUri, OutputStream os) throws MalformedRequestException, ResourceNotFoundException, BundlerProcessingException {
		try {
			File baseDir = new File(app.dir(), requestUri.scopePath);
			BundlableNode bundlableNode = app.root().locateFirstBundlableAncestorNode(baseDir);
			
			if(bundlableNode == null) {
				throw new ResourceNotFoundException("No bundlable resource could be found above the directory '" + baseDir.getPath() + "'");
			}
			else {
				BundlerPlugin bundler = app.root().bundler(getResourceBundlerName(requestUri));
				ParsedRequest parsedRequest = bundler.getRequestParser().parse(requestUri.logicalPath);
				
				// we're currently de-encapsulating the request parser within the bundler since this would allow bundlers
				// to safely route messages to other bundlers
				bundler.handleRequest(parsedRequest, bundlableNode.getBundleSet(), os);
			}
		}
		catch(ModelOperationException e) {
			throw new BundlerProcessingException(e);
		}
	}
	
	// TODO: move this method within RequestParser
	private String getResourceBundlerName(BladerunnerUri requestUri) {
		return requestUri.logicalPath.substring(requestUri.logicalPath.indexOf('/') + 1).replaceAll("\\.bundle$", "");
	}
}
