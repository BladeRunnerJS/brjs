package org.bladerunnerjs.core.plugin.bundler.js;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.bundlesource.BundleSourcePlugin;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.model.utility.RequestParserBuilder;


public class JsBundlerPlugin implements BundlerPlugin {
	private RequestParser requestParser;
	private BRJS brjs;
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
		
		RequestParserBuilder requestParserBuilder = new RequestParserBuilder();
		requestParserBuilder.accepts("js/js.bundle").as("bundle-request")
			.and("js/module/<module>.js").as("single-module-request");
		
		for(BundleSourcePlugin bundleSourcePlugin : brjs.bundleSourcePlugins()) {
			bundleSourcePlugin.configureRequestParser(requestParserBuilder);
		}
		
		requestParser = requestParserBuilder.build();
	}
	
	@Override
	public String getTagName() {
		return "js";
	}
	
	@Override
	public void writeDevContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		try {
			writeTagContent(bundleSet, generateRequiredDevRequestPaths(bundleSet, locale), writer);
		}
		catch (BundlerProcessingException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public void writeProdContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		try {
			writeTagContent(bundleSet, generateRequiredProdRequestPaths(bundleSet, locale), writer);
		}
		catch (BundlerProcessingException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public RequestParser getRequestParser() {
		return requestParser;
	}
	
	@Override
	public List<String> generateRequiredDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		List<String> requestPaths = new ArrayList<>();
		
		for(BundleSourcePlugin bundleSourcePlugin : brjs.bundleSourcePlugins()) {
			requestPaths.addAll(bundleSourcePlugin.generateRequiredDevRequestPaths(bundleSet, locale));
		}
		
		return requestPaths;
	}
	
	@Override
	public List<String> generateRequiredProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		List<String> requestPaths = new ArrayList<>();
		requestPaths.add(requestParser.createRequest("bundle-request"));
		
		for(BundleSourcePlugin bundleSourcePlugin : brjs.bundleSourcePlugins()) {
			requestPaths.addAll(bundleSourcePlugin.generateRequiredProdRequestPaths(bundleSet, locale));
		}
		
		return requestPaths;
	}
	
	@Override
	public void handleRequest(ParsedRequest request, BundleSet bundleSet, OutputStream os) throws ResourceNotFoundException, BundlerProcessingException {
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
				getBundleSourcePluginForRequest(request).handleRequest(request, bundleSet, os);
			}
		}
		catch(ConfigException | IOException e) {
			throw new BundlerProcessingException(e);
		}
	}
	
	private BundleSourcePlugin getBundleSourcePluginForRequest(ParsedRequest request) throws BundlerProcessingException {
		for(BundleSourcePlugin bundleSourcePlugin : brjs.bundleSourcePlugins()) {
			if(bundleSourcePlugin.handlesRequestForm(request.formName)) {
				return bundleSourcePlugin;
			}
		}
		
		throw new BundlerProcessingException("request form '" + request.formName + "' was not handled by any of the available bunlde sources.");
	}
	
	private void writeTagContent(BundleSet bundleSet, List<String> bundlerRequestPaths, Writer writer) throws IOException {
		for(BundleSourcePlugin bundleSourcePlugin : brjs.bundleSourcePlugins()) {
			bundleSourcePlugin.getTagAppender().writePreTagContent(bundleSet, writer);
		}
		
		for(String bundlerRequestPath : bundlerRequestPaths) {
			writer.write("<script type='text/javascript' src='" + bundlerRequestPath + "'></script>\n");
		}
		
		for(BundleSourcePlugin bundleSourcePlugin : brjs.bundleSourcePlugins()) {
			bundleSourcePlugin.getTagAppender().writePostTagContent(bundleSet, writer);
		}
	}
}
