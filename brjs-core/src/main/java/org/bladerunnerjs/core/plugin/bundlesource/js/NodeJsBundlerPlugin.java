package org.bladerunnerjs.core.plugin.bundlesource.js;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.bundlesource.FileSetFactory;
import org.bladerunnerjs.model.AssetFile;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.FileSet;
import org.bladerunnerjs.model.AssetFileFactory;
import org.bladerunnerjs.model.LinkedAssetFile;
import org.bladerunnerjs.model.NullFileSet;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.Resources;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.SourceLocation;
import org.bladerunnerjs.model.StandardFileSet;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.utility.RequestParserBuilder;

public class NodeJsBundlerPlugin implements BundlerPlugin {
	private RequestParser requestParser;
	private List<String> prodRequestPaths = new ArrayList<>();
	private BRJS brjs;
	
	{
		RequestParserBuilder requestParserBuilder = new RequestParserBuilder();
		requestParserBuilder.accepts("node-js/js.bundle").as("bundle-request")
			.and("node-js/module/<module>.js").as("single-module-request");
		
		requestParser = requestParserBuilder.build();
		prodRequestPaths.add(requestParser.createRequest("bundle-request"));
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
	}
	
	@Override
	public String getTagName() {
		return "node-js";
	}
	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		writeTagContent(bundleSet, locale, writer);
	}
	
	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		writeTagContent(bundleSet, locale, writer);
	}
	
	@Override
	public String getMimeType() {
		return "text/javascript";
	}
	
	@Override
	public RequestParser getRequestParser() {
		return requestParser;
	}
	
	@Override
	public FileSetFactory getFileSetFactory() {
		return new NodeJsBundleSourceFileSetFactory();
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
		return prodRequestPaths;
	}
	
	@Override
	public void handleRequest(ParsedRequest request, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException {
		try {
			if(request.formName.equals("single-module-request")) {
				Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding());
				
				SourceFile jsModule = bundleSet.getBundlableNode().sourceFile(request.properties.get("module"));
				IOUtils.copy(jsModule.getReader(), writer);
			}
			else if(request.formName.equals("bundle-request")) {
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding())) {
					for(SourceFile sourceFile : bundleSet.getSourceFiles()) {
						writer.write("// " + sourceFile.getRequirePath() + "\n");
						IOUtils.copy(sourceFile.getReader(), writer);
						writer.write("\n\n");
					}
				}
			}
			else {
				throw new BundlerProcessingException("unknown request form '" + request.formName + "'.");
			}
		}
		catch(ConfigException | IOException e) {
			throw new BundlerProcessingException(e);
		}
	}
	
	private void writeTagContent(BundleSet bundleSet, String locale, Writer writer) throws IOException {
		try {
			for(String bundlerRequestPath : generateRequiredDevRequestPaths(bundleSet, locale)) {
				writer.write("<script type='text/javascript' src='" + bundlerRequestPath + "'></script>\n");
			}
		}
		catch (BundlerProcessingException e) {
			throw new IOException(e);
		}
	}
	
	private class NodeJsBundleSourceFileSetFactory implements FileSetFactory {
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
	
	private class NodeJsFileSetFactory implements AssetFileFactory<SourceFile> {
		@Override
		public NodeJsSourceFile createFile(SourceLocation sourceLocation, String filePath) {
			return new NodeJsSourceFile(sourceLocation, filePath);
		}
	}
}
