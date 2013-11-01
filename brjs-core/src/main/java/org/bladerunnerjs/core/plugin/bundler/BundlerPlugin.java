package org.bladerunnerjs.core.plugin.bundler;

import java.io.OutputStream;
import java.util.List;

import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.TagHandlerPlugin;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;


public interface BundlerPlugin extends TagHandlerPlugin {
	RequestParser getRequestParser();
	List<String> generateRequiredDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException;
	List<String> generateRequiredProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException;
	void handleRequest(ParsedRequest request, BundleSet bundleSet, OutputStream os) throws ResourceNotFoundException, BundlerProcessingException;
}
