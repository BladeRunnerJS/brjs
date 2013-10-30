package org.bladerunnerjs.core.plugin.bundlesource;

import java.io.OutputStream;
import java.util.List;

import org.bladerunnerjs.model.AssetFile;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.FileSet;
import org.bladerunnerjs.model.LinkedAssetFile;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.SourceLocation;
import org.bladerunnerjs.model.TagAppender;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.utility.RequestParserBuilder;

public interface BundleSourcePlugin {
	String getMimeType();
	TagAppender getTagAppender();
	void configureRequestParser(RequestParserBuilder requestParserBuilder);
	void setRequestParser(RequestParser requestParser);
	List<String> generateRequiredDevRequestPaths(BundlableNode bundlableNode, String locale) throws BundlerProcessingException;
	List<String> generateRequiredProdRequestPaths(BundlableNode bundlableNode, String locale) throws BundlerProcessingException;
	boolean handlesRequestForm(String formName);
	void handleRequest(ParsedRequest request, BundlableNode bundlableNode, OutputStream os) throws BundlerProcessingException;
	FileSet<SourceFile> getSourceFileSet(SourceLocation sourceLocation);
	FileSet<LinkedAssetFile> getSeedResourceFileSet(SourceLocation sourceLocation);
	FileSet<AssetFile> getResourceFileSet(SourceLocation sourceLocation);
}
