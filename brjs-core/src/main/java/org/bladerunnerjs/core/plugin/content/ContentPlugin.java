package org.bladerunnerjs.core.plugin.content;

import java.io.OutputStream;
import java.util.List;

import org.bladerunnerjs.core.plugin.Plugin;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ContentPathParser;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

public interface ContentPlugin extends Plugin {
	String getRequestPrefix();
	String getMimeType();
	ContentPathParser getContentPathParser();
	void writeContent(ParsedContentPath path, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException;
	List<String> getValidDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException;
	List<String> getValidProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException;
}
