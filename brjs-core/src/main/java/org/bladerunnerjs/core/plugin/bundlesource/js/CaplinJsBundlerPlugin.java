package org.bladerunnerjs.core.plugin.bundlesource.js;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.model.AssetFile;
import org.bladerunnerjs.model.AssetFileAccessor;
import org.bladerunnerjs.model.AbstractAssetFileFactory;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.LinkedAssetFile;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.AssetContainer;
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
		public List<SourceFile> getSourceFiles(AssetContainer assetContainer)
		{
			// TODO: switch over to this simpler AssetLocationUtility once all the AssetFileAccessor methods are passed an AssetLocation
//			if(JsStyleUtility.getJsStyle(assetLocation.dir().equals("caplin-js"))) {
//				return AssetLocationUtility.populateFileList(new ArrayList<SourceFile>, assetLocation, "js", CaplinJsSourceFile.class);
//			}
//			else {
//				return Arrays.asList();
//			}
			
			//TODO: remove this "src" - it should be known by the model
			return new CaplinJsFileSetFactory().findFiles(assetContainer, assetContainer.file("src"), new SuffixFileFilter("js"), TrueFileFilter.INSTANCE);
		}

		@Override
		public List<LinkedAssetFile> getLinkedResourceFiles(AssetLocation assetLocation)
		{
			return Arrays.asList();
		}

		@Override
		public List<AssetFile> getResourceFiles(AssetLocation assetLocation)
		{
			return Arrays.asList();
		}
		
	}
	
	class CaplinJsFileSetFactory extends AbstractAssetFileFactory<SourceFile>
	{
		@Override
		public CaplinJsSourceFile createFile(AssetContainer assetContainer, File file)
		{
			return new CaplinJsSourceFile(assetContainer, file);
		}
	}
	
}
