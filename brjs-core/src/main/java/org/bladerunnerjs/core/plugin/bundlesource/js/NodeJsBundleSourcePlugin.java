package org.bladerunnerjs.core.plugin.bundlesource.js;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.core.plugin.bundlesource.BundleSourceFileSetFactory;
import org.bladerunnerjs.core.plugin.bundlesource.BundleSourcePlugin;
import org.bladerunnerjs.model.AssetFile;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.FileSet;
import org.bladerunnerjs.model.FileSetFactory;
import org.bladerunnerjs.model.LinkedAssetFile;
import org.bladerunnerjs.model.NullFileSet;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.Resources;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.SourceLocation;
import org.bladerunnerjs.model.StandardFileSet;
import org.bladerunnerjs.model.TagAppender;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.utility.RequestParserBuilder;

public class NodeJsBundleSourcePlugin implements BundleSourcePlugin {
	private RequestParser requestParser;
	
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}
	
	@Override
	public String getMimeType() {
		return "text/javascript";
	}
	
	@Override
	public TagAppender getTagAppender() {
		return new NullTagAppender();
	}
	
	@Override
	public BundleSourceFileSetFactory getFileSetFactory() {
		return new NodeJsBundleSourceFileSetFactory();
	}
	
	@Override
	public void configureRequestParser(RequestParserBuilder requestParserBuilder) {
		// do nothing
	}
	
	@Override
	public void setRequestParser(RequestParser requestParser) {
		this.requestParser = requestParser;
	}
	
	@Override
	public List<String> generateRequiredDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		List<String> requestPaths = new ArrayList<>();
		
		for(SourceFile sourceFile : bundleSet.getSourceFiles()) {
			if(sourceFile instanceof NodeJsSourceFile) {
				requestPaths.add(requestParser.createRequest("single-module-request", sourceFile.getRequirePath()));
			}
		}
		
		return requestPaths;
	}
	
	@Override
	public List<String> generateRequiredProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		return new ArrayList<>();
	}
	
	@Override
	public boolean handlesRequestForm(String formName) {
		return false;
	}
	
	@Override
	public void handleRequest(ParsedRequest request, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException {
		// do nothing
	}
	
	private class NodeJsBundleSourceFileSetFactory implements BundleSourceFileSetFactory {
		@Override
		public FileSet<LinkedAssetFile> getLinkedResourceFileSet(Resources resources) {
			return new NullFileSet<LinkedAssetFile>();
		}
		
		@Override
		public FileSet<SourceFile> getSourceFileSet(SourceLocation sourceLocation) {
			return new StandardFileSet<SourceFile>(sourceLocation, StandardFileSet.paths("src/**/*.js"), null, new NodeJsFileSetFactory());
		}
		
		@Override
		public FileSet<AssetFile> getResourceFileSet(Resources resources) {
			return new NullFileSet<AssetFile>();
		}
	}
	
	private class NodeJsFileSetFactory implements FileSetFactory<SourceFile> {
		@Override
		public NodeJsSourceFile createFile(SourceLocation sourceLocation, String filePath) {
			return new NodeJsSourceFile(sourceLocation, filePath);
		}
	}
}
