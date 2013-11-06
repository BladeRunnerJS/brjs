package org.bladerunnerjs.core.plugin.bundler;

import java.io.OutputStream;
import java.util.List;

import org.bladerunnerjs.core.plugin.bundlesource.FileSetFactory;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.TagHandlerPlugin;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;


public interface BundlerPlugin extends TagHandlerPlugin {
	String getMimeType();
	FileSetFactory getFileSetFactory();
	RequestParser getRequestParser();
	List<String> generateRequiredDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException;
	List<String> generateRequiredProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException;
	void handleRequest(ParsedRequest request, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException;
}
