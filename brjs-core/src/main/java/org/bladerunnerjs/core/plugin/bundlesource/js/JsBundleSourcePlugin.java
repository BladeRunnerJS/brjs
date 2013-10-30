package org.bladerunnerjs.core.plugin.bundlesource.js;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.core.plugin.bundlesource.BundleSourcePlugin;
import org.bladerunnerjs.model.AssetFile;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.FileSet;
import org.bladerunnerjs.model.LinkedAssetFile;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.SourceLocation;
import org.bladerunnerjs.model.TagAppender;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.utility.RequestParserBuilder;

public class JsBundleSourcePlugin implements BundleSourcePlugin {
	private RequestParser requestParser;
	
	@Override
	public String getMimeType() {
		return "text/javascript";
	}
	
	@Override
	public TagAppender getTagAppender() {
		return new JsBundleSourceTagAppender();
	}
	
	@Override
	public void configureRequestParser(RequestParserBuilder requestParserBuilder) {
		// do nothing
	}
	
	@Override
	public void setRequestParser(RequestParser requestParser) {
		this.requestParser = requestParser;
	}
	
	// TODO: should we only be passing the bundle-set at this point, since we don't want it recalculated for every bundle-source?
	@Override
	public List<String> generateRequiredDevRequestPaths(BundlableNode bundlableNode, String locale) throws BundlerProcessingException {
		List<String> requestPaths = new ArrayList<>();
		
		try {
			// TODO: we should only be returning source-files related to this bundle-source
			for(SourceFile sourceFile : bundlableNode.getBundleSet().getSourceFiles()) {
				requestPaths.add(requestParser.createRequest("single-module-request", sourceFile.getRequirePath()));
			}
		}
		catch (ModelOperationException e) {
			throw new BundlerProcessingException(e);
		}
		
		return requestPaths;
	}
	
	@Override
	public List<String> generateRequiredProdRequestPaths(BundlableNode bundlableNode, String locale) throws BundlerProcessingException {
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
				// TODO: ensure we use the correct character encoding
				try (Writer writer = new OutputStreamWriter(os)) {
					// TODO: we should only be returning source-files related to this bundle-source
					for(SourceFile sourceFile : bundlableNode.getBundleSet().getSourceFiles()) {
						writer.write("// " + sourceFile.getRequirePath() + "\n");
						IOUtils.copy(sourceFile.getReader(), writer);
						writer.write("\n\n");
					}
				}
			}
			catch(IOException | ModelOperationException e) {
				throw new BundlerProcessingException(e);
			}
		}
	}
	
	@Override
	public FileSet<SourceFile> getSourceFileSet(SourceLocation sourceLocation) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public FileSet<LinkedAssetFile> getSeedResourceFileSet(SourceLocation sourceLocation) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public FileSet<AssetFile> getResourceFileSet(SourceLocation sourceLocation) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class JsBundleSourceTagAppender implements TagAppender {
		@Override
		public void writeTagContent(List<String> bundlerRequestPaths, Writer writer) throws IOException {
			// TODO Auto-generated method stub
		}
	}
}
