package org.bladerunnerjs.plugin;

import java.io.OutputStream;
import java.util.List;

import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.utility.ContentPathParser;

public interface ContentPlugin extends Plugin {
	String getRequestPrefix();
	String getMimeType();
	ContentPathParser getContentPathParser();
	void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException;
	List<String> getValidDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException;
	List<String> getValidProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException;
}
