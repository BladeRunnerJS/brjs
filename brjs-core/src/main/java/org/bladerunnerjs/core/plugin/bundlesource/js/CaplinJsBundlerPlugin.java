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
import org.bladerunnerjs.core.plugin.bundler.AbstractBundlerPlugin;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.taghandler.TagHandlerPlugin;
import org.bladerunnerjs.model.AssetFile;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.LinkedAssetFile;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ContentPathParser;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.utility.JsStyleUtility;
import org.bladerunnerjs.model.utility.RequestParserBuilder;
import org.json.simple.JSONObject;

public class CaplinJsBundlerPlugin extends AbstractBundlerPlugin implements BundlerPlugin, TagHandlerPlugin {
	public static final String JS_STYLE = "caplin-js";
	
	private ContentPathParser requestParser;
	private List<String> prodRequestPaths = new ArrayList<>();
	private BRJS brjs;
	
	{
		RequestParserBuilder requestParserBuilder = new RequestParserBuilder();
		requestParserBuilder
			.accepts("caplin-js/bundle.js").as("bundle-request")
				.and("caplin-js/module/<module>.js").as("single-module-request")
				.and("caplin-js/package-definitions.js").as("package-definitions-request")
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
		return getRequestPrefix();
	}
	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		try {
			writeTagContent(bundleSet, getValidDevRequestPaths(bundleSet, locale), writer);
		}
		catch (BundlerProcessingException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		try {
			writeTagContent(bundleSet, getValidProdRequestPaths(bundleSet, locale), writer);
		}
		catch (BundlerProcessingException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public String getRequestPrefix() {
		return "caplin-js";
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
		
		requestPaths.add(requestParser.createRequest("package-definitions-request"));
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
					globalizeNonCaplinJsClasses((CaplinJsSourceFile) jsModule, writer);
				}
			}
			else if(request.formName.equals("bundle-request")) {
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding())) {
								
					Map<String, Map<String, ?>> packageStructure = createPackageStructureForCaplinJsClasses(bundleSet, writer);
    				writePackageStructure(packageStructure, writer);
    				writer.write("\n");
					
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
			else if(request.formName.equals("package-definitions-request")) {
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding())) {
    				Map<String, Map<String, ?>> packageStructure = createPackageStructureForCaplinJsClasses(bundleSet, writer);
    				writePackageStructure(packageStructure, writer);
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
		if (JsStyleUtility.getJsStyle(assetLocation).equals(JS_STYLE)) {
			// TODO: blow up if the package of the assetLocation would not be a valid namespace
			
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
	
	private void writeTagContent(BundleSet bundleSet, List<String> requestPaths, Writer writer) throws IOException {
		for(String bundlerRequestPath : requestPaths) {
			writer.write("<script type='text/javascript' src='" + bundlerRequestPath + "'></script>\n");
		}
	}
	
	private Map<String, Map<String, ?>> createPackageStructureForCaplinJsClasses(BundleSet bundleSet, Writer writer) {
		Map<String, Map<String, ?>> packageStructure = new HashMap<>();
		
		for(SourceFile sourceFile : bundleSet.getSourceFiles()) {
			if(sourceFile instanceof CaplinJsSourceFile) {
				List<String> packageList = Arrays.asList(sourceFile.getRequirePath().split("/"));
				addPackageToStructure(packageStructure, packageList.subList(0, packageList.size() - 1));
			}
		}
		
		return packageStructure;
	}
	
	@SuppressWarnings("unchecked")
	private void addPackageToStructure(Map<String, Map<String, ?>> packageStructure, List<String> packageList)
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
			
			writer.flush();
		}
	}
	
	private void globalizeNonCaplinJsClasses(BundleSet bundleSet, Writer writer) throws IOException, BundlerProcessingException {
		for(SourceFile sourceFile : bundleSet.getSourceFiles()) {
			if(sourceFile instanceof CaplinJsSourceFile) {
				globalizeNonCaplinJsClasses((CaplinJsSourceFile) sourceFile, writer);
			}
		}
	}
	
	private void globalizeNonCaplinJsClasses(CaplinJsSourceFile sourceFile, Writer writer) throws IOException, BundlerProcessingException {
		try {
			for(SourceFile dependentSourceFile : sourceFile.getDependentSourceFiles()) {
				if(!(dependentSourceFile instanceof CaplinJsSourceFile)) {
					String moduleNamespace = dependentSourceFile.getRequirePath().replaceAll("/", ".");
					writer.write(moduleNamespace + " = require('" + dependentSourceFile.getRequirePath()  + "');\n");
				}
			}
		}
		catch(ModelOperationException e) {
			throw new BundlerProcessingException(e);
		}
	}
}
