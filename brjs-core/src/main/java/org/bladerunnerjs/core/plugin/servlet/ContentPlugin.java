package org.bladerunnerjs.core.plugin.servlet;

import java.io.OutputStream;
import java.util.List;

import org.bladerunnerjs.core.plugin.Plugin;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

public interface ContentPlugin extends Plugin {
	String getMimeType();
	RequestParser getRequestParser();
	void writeContent(ParsedRequest request, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException;
	List<String> generateRequiredDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException;
	List<String> generateRequiredProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException;
}
