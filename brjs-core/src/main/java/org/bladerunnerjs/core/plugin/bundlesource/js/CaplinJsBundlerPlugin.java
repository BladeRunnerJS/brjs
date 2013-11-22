package org.bladerunnerjs.core.plugin.bundlesource.js;

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
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.model.AssetFile;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.LinkedAssetFile;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ContentPathParser;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.utility.JsStyleUtility;
import org.bladerunnerjs.model.utility.RequestParserBuilder;
import org.json.simple.JSONObject;

public class CaplinJsBundlerPlugin implements BundlerPlugin {
	private ContentPathParser requestParser;
	private List<String> prodRequestPaths = new ArrayList<>();
	private BRJS brjs;
	
	{
		RequestParserBuilder requestParserBuilder = new RequestParserBuilder();
		requestParserBuilder
			.accepts("caplin-js/js.bundle").as("bundle-request")
				.and("caplin-js/module/<module>/js.bundle").as("single-module-request")
			.where("module").hasForm(".+"); // TODO: ensure we really need such a simple hasForm() -- we didn't use to need it
		
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
	public ContentPathParser getContentPathParser() {
		return requestParser;
	}
	
	@Override
	public List<String> getValidDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		List<String> requestPaths = new ArrayList<>();
		
		for(SourceFile sourceFile : bundleSet.getSourceFiles()) {
			if(sourceFile instanceof CaplinJsSourceFile) {
				requestPaths.add(requestParser.createRequest("single-module-request", sourceFile.getRequirePath()));
			}
		}
		
		return requestPaths;
	}
	
	@Override
	public List<String> getValidProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		return prodRequestPaths;
	}
	
	@Override
	public void writeContent(ParsedContentPath request, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException {
		try {
			if(request.formName.equals("single-module-request")) {
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding())) {
					SourceFile jsModule = bundleSet.getBundlableNode().getSourceFile(request.properties.get("module"));
					IOUtils.copy(jsModule.getReader(), writer);
				}
			}
			else if(request.formName.equals("bundle-request")) {
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding())) {
					
					Map<String, Map<String, ?>> packageStructure = createPackageStructureForCaplinJsClasses(bundleSet, writer);
					writePackageStructure(packageStructure, writer);
					
					for(SourceFile sourceFile : bundleSet.getSourceFiles()) {
						if(sourceFile instanceof CaplinJsSourceFile)
						{
    						writer.write("// " + sourceFile.getRequirePath() + "\n");
    						IOUtils.copy(sourceFile.getReader(), writer);
    						writer.write("\n\n");
						}
					}
					
					globalizeNonCaplinJsClasses(bundleSet, writer);
				}
			}
			else {
				throw new BundlerProcessingException("unknown request form '" + request.formName + "'.");
			}
		}
		catch(ConfigException | IOException | RequirePathException e) {
			throw new BundlerProcessingException(e);
		}
	}
	
	@Override
	public List<SourceFile> getSourceFiles(AssetLocation assetLocation)
	{
		if(JsStyleUtility.getJsStyle(assetLocation.dir()).equals("caplin-js")) {
			return assetLocation.getAssetContainer().root().getAssetFilesWithExtension(assetLocation, CaplinJsSourceFile.class, "js");
		}
		else {
			return Arrays.asList();
		}
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
	
	private void writeTagContent(BundleSet bundleSet, String locale, Writer writer) throws IOException {
		try {
			for(String bundlerRequestPath : getValidDevRequestPaths(bundleSet, locale)) {
				writer.write("<script type='text/javascript' src='" + bundlerRequestPath + "'></script>\n");
			}
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
				
				writer.write(caplinSourceFile.getClassName() + " = require('" + caplinSourceFile.getRequirePath()  + "');\n");
			}
		}
	}
}
