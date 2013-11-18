package org.bladerunnerjs.core.plugin.servlet;

import java.io.OutputStream;

import org.bladerunnerjs.core.plugin.Plugin;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

public interface ServletPlugin extends Plugin {
	String getMimeType();
	RequestParser getRequestParser();
	void handleRequest(ParsedRequest request, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException;
}
