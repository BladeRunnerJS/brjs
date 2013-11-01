package org.bladerunnerjs.core.plugin.bundlesource;

import java.io.OutputStream;
import java.util.List;

import org.bladerunnerjs.core.plugin.Plugin;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.TagAppender;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.utility.RequestParserBuilder;

public interface BundleSourcePlugin extends Plugin {
	String getMimeType();
	TagAppender getTagAppender();
	BundleSourceFileSetFactory getFileSetFactory();
	void configureRequestParser(RequestParserBuilder requestParserBuilder);
	void setRequestParser(RequestParser requestParser);
	List<String> generateRequiredDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException;
	List<String> generateRequiredProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException;
	boolean handlesRequestForm(String formName);
	void handleRequest(ParsedRequest request, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException;
}
