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
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.model.utility.RequestParserBuilder;


public class JsBundlerPlugin implements BundlerPlugin {
	private final RequestParser requestParser;
	private List<BundleSourcePlugin> bundleSourcePlugins;
	private BRJS brjs;
	
	{
		RequestParserBuilder requestParserBuilder = new RequestParserBuilder();
		requestParserBuilder.accepts("js/bundle.js").as("bundle-request")
			.and("js/module/<module>.js").as("single-module-request");
		
		for(BundleSourcePlugin bundleSourcePlugin : bundleSourcePlugins) {
			bundleSourcePlugin.configureRequestParser(requestParserBuilder);
		}
		
		requestParser = requestParserBuilder.build();
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
	}
	
	@Override
	public String getTagName() {
		return "js";
	}
	
	@Override
	public void writeDevContent(Map<String, String> tagAttributes, BundlableNode bundlableNode, String locale, Writer writer) throws IOException {
		try {
			writeTagContent(generateRequiredDevRequestPaths(bundlableNode, locale), writer);
		}
		catch (BundlerProcessingException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public void writeProdContent(Map<String, String> tagAttributes, BundlableNode bundlableNode, String locale, Writer writer) throws IOException {
		try {
			writeTagContent(generateRequiredProdRequestPaths(bundlableNode, locale), writer);
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
	public List<String> generateRequiredDevRequestPaths(BundlableNode bundlableNode, String locale) throws BundlerProcessingException {
		List<String> requestPaths = new ArrayList<>();
		
		try {
			BundleSet bundleSet = bundlableNode.getBundleSet();
			
			for(BundleSourcePlugin bundleSourcePlugin : bundleSourcePlugins) {
				requestPaths.addAll(bundleSourcePlugin.generateRequiredDevRequestPaths(bundleSet, locale));
			}
		}
		catch(ModelOperationException e) {
			throw new BundlerProcessingException(e);
		}
		
		return requestPaths;
	}
	
	@Override
	public List<String> generateRequiredProdRequestPaths(BundlableNode bundlableNode, String locale) throws BundlerProcessingException {
		List<String> requestPaths = new ArrayList<>();
		
		try {
			BundleSet bundleSet = bundlableNode.getBundleSet();
			
			requestPaths.add(requestParser.createRequest("bundle-request"));
			
			for(BundleSourcePlugin bundleSourcePlugin : bundleSourcePlugins) {
				requestPaths.addAll(bundleSourcePlugin.generateRequiredProdRequestPaths(bundleSet, locale));
			}
		}
		catch(ModelOperationException e) {
			throw new BundlerProcessingException(e);
		}
		
		return requestPaths;
	}
	
	@Override
	public void handleRequest(ParsedRequest request, BundlableNode bundlableNode, OutputStream os) throws ResourceNotFoundException, BundlerProcessingException {
		try {
			if(request.formName.equals("single-module-request")) {
				Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding());
				
				SourceFile jsModule = bundlableNode.sourceFile(request.properties.get("module"));
				IOUtils.copy(jsModule.getReader(), writer);
			}
			else if(request.formName.equals("bundle-request")) {
				for(BundleSourcePlugin bundleSourcePlugin : bundleSourcePlugins) {
					bundleSourcePlugin.handleRequest(request, bundlableNode, os);
				}
			}
			else {
				boolean requestHandled = false;
				
				for(BundleSourcePlugin bundleSourcePlugin : bundleSourcePlugins) {
					if(bundleSourcePlugin.handlesRequestForm(request.formName)) {
						bundleSourcePlugin.handleRequest(request, bundlableNode, os);
						requestHandled = true;
						break;
					}
				}
				
				if(!requestHandled) {
					throw new BundlerProcessingException("request form '" + request.formName + "' was not handled by any of the available bunlde sources.");
				}
			}
		}
		catch(ConfigException | IOException e) {
			throw new BundlerProcessingException(e);
		}
	}
	
	private void writeTagContent(List<String> bundlerRequestPaths, Writer writer) throws IOException {
		for(BundleSourcePlugin bundleSourcePlugin : bundleSourcePlugins) {
			bundleSourcePlugin.getTagAppender().writePreTagContent(bundlerRequestPaths, writer);
		}
		
		for(String bundlerRequestPath : bundlerRequestPaths) {
			writer.write("<script type='text/javascript' src='" + bundlerRequestPath + "'></script>\n");
		}
		
		for(BundleSourcePlugin bundleSourcePlugin : bundleSourcePlugins) {
			bundleSourcePlugin.getTagAppender().writePostTagContent(bundlerRequestPaths, writer);
		}
	}
}
