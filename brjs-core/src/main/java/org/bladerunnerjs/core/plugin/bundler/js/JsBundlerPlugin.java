package org.bladerunnerjs.core.plugin.bundler.js;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.model.utility.RequestParserBuilder;


public class JsBundlerPlugin implements BundlerPlugin {
	private final RequestParser requestParser;
	{
		RequestParserBuilder requestParserBuilder = new RequestParserBuilder();
		requestParserBuilder.accepts("js/bundle.js").as("bundle-request")
			.and("js/module/<module>.js").as("source-file-request");
		requestParser = requestParserBuilder.build();
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
	}
	
	@Override
	public String getMimeType() {
		return "text/javascript";
	}
	
	@Override
	public String getBundlerName() {
		return "js";
	}
	
	@Override
	public RequestParser getRequestParser() {
		return requestParser;
	}
	
	@Override
	public List<String> generateRequiredDevRequestPaths(BundlableNode bundlableNode, String locale) throws BundlerProcessingException {
		List<String> requests = new ArrayList<>();
		
		try {
			for(SourceFile sourceFile : bundlableNode.getBundleSet().getSourceFiles()) {
				requests.add(requestParser.createRequest("source-file-request", sourceFile.getRequirePath()));
			}
		}
		catch(ModelOperationException e) {
			throw new BundlerProcessingException(e);
		}
		
		return requests;
	}
	
	@Override
	public List<String> generateRequiredProdRequestPaths(BundlableNode bundlableNode, String locale) {
		List<String> requests = new ArrayList<>();
		requests.add(requestParser.createRequest("bundle-request", getBundlerName()));
		
		return requests;
	}
	
	@Override
	public void handleRequest(ParsedRequest request, BundlableNode bundlableNode, OutputStream os) throws ResourceNotFoundException, BundlerProcessingException {
		try {
			// TODO: ensure we use the correct character encoding
			Writer writer = new OutputStreamWriter(os);
			
			if(request.formName.equals("source-file-request")) {
				SourceFile jsModule = bundlableNode.sourceFile(request.properties.get("module"));
				IOUtils.copy(jsModule.getReader(), writer);
			}
			else if(request.formName.equals("bundle-request")) {
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
