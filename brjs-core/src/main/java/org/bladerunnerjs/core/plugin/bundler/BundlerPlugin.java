package org.bladerunnerjs.core.plugin.bundler;

import java.io.OutputStream;
import java.util.List;

import org.bladerunnerjs.core.plugin.Plugin;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;


public interface BundlerPlugin extends Plugin {
	String getMimeType();
	String getBundlerName();
	RequestParser getRequestParser();
	List<String> generateRequiredDevRequestPaths(BundlableNode bundlableNode, String locale) throws BundlerProcessingException;
	List<String> generateRequiredProdRequestPaths(BundlableNode bundlableNode, String locale) throws BundlerProcessingException;
	void handleRequest(ParsedRequest request, BundlableNode bundlableNode, OutputStream os) throws ResourceNotFoundException, BundlerProcessingException;
}
