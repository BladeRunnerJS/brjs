package org.bladerunnerjs.core.plugin.bundler.js;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.bundlesource.FileSetFactory;
import org.bladerunnerjs.core.plugin.bundlesource.NullFileSetFactory;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.utility.RequestParserBuilder;


public class CompositeJsBundlerPlugin implements BundlerPlugin {
	private RequestParser nullRequestParser = (new RequestParserBuilder()).build();
	private BRJS brjs;
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
	}
	
	@Override
	public String getTagName() {
		return "js";
	}
	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		for(BundlerPlugin bundlerPlugin : brjs.bundlerPlugins()) {
			if((bundlerPlugin != this) && (bundlerPlugin.getMimeType().equals("text/javascript"))) {
				bundlerPlugin.writeDevTagContent(tagAttributes, bundleSet, locale, writer);
			}
		}
	}
	
	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		for(BundlerPlugin bundlerPlugin : brjs.bundlerPlugins()) {
			if((bundlerPlugin != this) && (bundlerPlugin.getMimeType().equals("text/javascript"))) {
				bundlerPlugin.writeProdTagContent(tagAttributes, bundleSet, locale, writer);
			}
		}
	}
	
	@Override
	public String getMimeType() {
		return "text/javascript";
	}
	
	@Override
	public FileSetFactory getFileSetFactory() {
		return new NullFileSetFactory();
	}
	
	@Override
	public RequestParser getRequestParser() {
		return nullRequestParser;
	}
	
	@Override
	public List<String> generateRequiredDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		List<String> requestPaths = new ArrayList<>();
		
		for(BundlerPlugin bundlerPlugin : brjs.bundlerPlugins()) {
			if((bundlerPlugin != this) && (bundlerPlugin.getMimeType().equals("text/javascript"))) {
				requestPaths.addAll(bundlerPlugin.generateRequiredDevRequestPaths(bundleSet, locale));
			}
		}
		
		return requestPaths;
	}
	
	@Override
	public List<String> generateRequiredProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		List<String> requestPaths = new ArrayList<>();
		
		for(BundlerPlugin bundlerPlugin : brjs.bundlerPlugins()) {
			if((bundlerPlugin != this) && (bundlerPlugin.getMimeType().equals("text/javascript"))) {
				requestPaths.addAll(bundlerPlugin.generateRequiredProdRequestPaths(bundleSet, locale));
			}
		}
		
		return requestPaths;
	}
	
	@Override
	public void handleRequest(ParsedRequest request, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException {
		// TODO: throw an exception
	}
}
