package org.bladerunnerjs.core.plugin.bundlesource.js;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.core.plugin.bundlesource.BundleSourceFileSetFactory;
import org.bladerunnerjs.core.plugin.bundlesource.BundleSourcePlugin;
import org.bladerunnerjs.model.AssetFile;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.FileSet;
import org.bladerunnerjs.model.LinkedAssetFile;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.SourceLocation;
import org.bladerunnerjs.model.TagAppender;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.utility.RequestParserBuilder;

public class CaplinJsBundleSourcePlugin implements BundleSourcePlugin {
	private RequestParser requestParser;
	private BRJS brjs;
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
	}
	
	@Override
	public String getMimeType() {
		return "text/javascript";
	}
	
	@Override
	public TagAppender getTagAppender() {
		return new JsBundleSourceTagAppender();
	}
	
	@Override
	public BundleSourceFileSetFactory getFileSetFactory() {
		return new JsBundleSourceFileSetFactory();
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
			// TODO: fix instanceof type -- we should only be returning source-files related to this bundle-source
			if(sourceFile instanceof SourceFile) {
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
	public void handleRequest(ParsedRequest request, BundlableNode bundlableNode, OutputStream os) throws BundlerProcessingException {
		if(request.formName.equals("bundle-request")) {
			try {
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding())) {
					for(SourceFile sourceFile : bundlableNode.getBundleSet().getSourceFiles()) {
						// TODO: fix instanceof type -- we should only be returning source-files related to this bundle-source
						if(sourceFile instanceof SourceFile) {
							writer.write("// " + sourceFile.getRequirePath() + "\n");
							IOUtils.copy(sourceFile.getReader(), writer);
							writer.write("\n\n");
						}
					}
				}
			}
			catch(ConfigException | IOException | ModelOperationException e) {
				throw new BundlerProcessingException(e);
			}
		}
	}
	
	private class JsBundleSourceTagAppender implements TagAppender {
		@Override
		public void writePreTagContent(List<String> bundlerRequestPaths, Writer writer) throws IOException {
			// TODO: create packages for all CaplinJs modules (if any Caplin style modules have been used)
		}
		
		@Override
		public void writePostTagContent(List<String> bundlerRequestPaths, Writer writer) throws IOException {
			// TODO: globalize any non CaplinJs modules (if any Caplin style modules have been used)
		}
	}
	
	private class JsBundleSourceFileSetFactory implements BundleSourceFileSetFactory {
		@Override
		public FileSet<LinkedAssetFile> getSeedFileSet(BundlableNode bundlableNode) {
			// TODO Auto-generated method stub
//			return new FileSet<CaplinJsSourceFile>(CaplinJsSourceFile.class, );
			return null;
		}
		
		@Override
		public FileSet<SourceFile> getSourceFileSet(SourceLocation sourceLocation) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public FileSet<AssetFile> getResourceFileSet(SourceLocation sourceLocation) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
