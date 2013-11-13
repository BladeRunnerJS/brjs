package org.bladerunnerjs.core.plugin.bundlesource.js;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.model.AssetFile;
import org.bladerunnerjs.model.AssetFileAccessor;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.AssetFileFactory;
import org.bladerunnerjs.model.LinkedAssetFile;
import org.bladerunnerjs.model.NullFileSet;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.Resources;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.SourceLocation;
import org.bladerunnerjs.model.StandardFileSet;
import org.bladerunnerjs.model.exception.AmbiguousRequirePathException;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.utility.RequestParserBuilder;
import org.json.simple.JSONObject;

public class CaplinJsBundlerPlugin implements BundlerPlugin {
	private RequestParser requestParser;
	private List<String> prodRequestPaths = new ArrayList<>();
	private BRJS brjs;
	
	{
		RequestParserBuilder requestParserBuilder = new RequestParserBuilder();
		requestParserBuilder
			.accepts("caplin-js/js.bundle").as("bundle-request")
				.and("caplin-js/module/<module>/js.bundle").as("single-module-request")
			.where("module").hasForm(".+");
		
		requestParser = requestParserBuilder.build();
		prodRequestPaths.add(requestParser.createRequest("bundle-request"));
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
	}
	
	@Override
	public String getTagName() {
		return "caplin-js";
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
	public AssetFileAccessor getAssetFileAccessor()
	{
		return new CaplinJsAssetFileAccessor();
	}
	
	@Override
	public List<String> generateRequiredDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		List<String> requestPaths = new ArrayList<>();
		
		for(SourceFile sourceFile : bundleSet.getSourceFiles()) {
			if(sourceFile instanceof CaplinJsSourceFile) {
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
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding())) {
					SourceFile jsModule = bundleSet.getBundlableNode().getSourceFile(request.properties.get("module"));
					IOUtils.copy(jsModule.getReader(), writer);
				}
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
		catch(ConfigException | IOException | AmbiguousRequirePathException e) {
			throw new BundlerProcessingException(e);
		}
	}
	
	private void writeTagContent(BundleSet bundleSet, String locale, Writer writer) throws IOException {
		try {
			Map<String, Map<String, ?>> packageStructure = createPackageStructureForCaplinJsClasses(bundleSet, writer);
			writePackageStructure(packageStructure, writer);
			
			for(String bundlerRequestPath : generateRequiredDevRequestPaths(bundleSet, locale)) {
				writer.write("<script type='text/javascript' src='" + bundlerRequestPath + "'></script>\n");
			}
			
			globalizeNonCaplinJsClasses(bundleSet, writer);
		}
		catch (BundlerProcessingException e) {
			throw new IOException(e);
		}
	}
	
	private Map<String, Map<String, ?>> createPackageStructureForCaplinJsClasses(BundleSet bundleSet, Writer writer) {
		Map<String, Map<String, ?>> packageStructure = new HashMap<>();
		
		for(SourceFile sourceFile : bundleSet.getSourceFiles()) {
			if(sourceFile instanceof CaplinJsSourceFile) {
				addPackageToStructure(packageStructure, sourceFile.getRequirePath().split("/"));
			}
		}
		
		return packageStructure;
	}
	
	@SuppressWarnings("unchecked")
	private void addPackageToStructure(Map<String, Map<String, ?>> packageStructure, String[] packageList)
	{
		Map<String, Map<String, ?>> currentPackage = packageStructure;
		
		for(String packageName : packageList)
		{
			Map<String, Map<String, ?>> nextPackage;
			
			if(currentPackage.containsKey(packageName))
			{
				nextPackage = (Map<String, Map<String, ?>>) currentPackage.get(packageName);
			}
			else
			{
				nextPackage = new HashMap<String, Map<String, ?>>();
				currentPackage.put(packageName, nextPackage);
			}
			
			currentPackage = nextPackage;
		}
	}
	
	private void writePackageStructure(Map<String, Map<String, ?>> packageStructure, Writer writer) throws IOException {
		if(packageStructure.size() > 0) {
			writer.write("// package definition block\n");
			
			for(String packageName : packageStructure.keySet()) {
				writer.write("window." + packageName + " = ");
				JSONObject.writeJSONString(packageStructure.get(packageName), writer);
				writer.write(";\n");
			}
			
			writer.write("\n");
			writer.flush();
		}
	}
	
	private void globalizeNonCaplinJsClasses(BundleSet bundleSet, Writer writer) throws IOException {
		for(SourceFile sourceFile : bundleSet.getSourceFiles()) {
			if(sourceFile instanceof CaplinJsSourceFile) {
				CaplinJsSourceFile caplinSourceFile = (CaplinJsSourceFile) sourceFile;
				
				writer.write(caplinSourceFile.getClassName() + " = require('" + caplinSourceFile.getRequirePath()  + "')");
			}
		}
	}
	
	
	private class CaplinJsAssetFileAccessor implements AssetFileAccessor
	{

		@Override
		public List<SourceFile> getSourceFiles(SourceLocation sourceLocation)
		{
			return new StandardFileSet<SourceFile>(sourceLocation, StandardFileSet.paths("src/**/*.js"), null, new CaplinJsFileSetFactory()).getFiles();
		}

		@Override
		public List<LinkedAssetFile> getLinkedResourceFiles(Resources resources)
		{
			return new NullFileSet<LinkedAssetFile>().getFiles();
		}

		@Override
		public List<AssetFile> getResourceFiles(Resources resources)
		{
			return new NullFileSet<AssetFile>().getFiles();
		}
		
	}
	
	//TODO: get rid of this
	private class CaplinJsFileSetFactory implements AssetFileFactory<SourceFile> {
		@Override
		public CaplinJsSourceFile createFile(SourceLocation sourceLocation, File file) {
			return new CaplinJsSourceFile(sourceLocation, file);
		}
	}
}
