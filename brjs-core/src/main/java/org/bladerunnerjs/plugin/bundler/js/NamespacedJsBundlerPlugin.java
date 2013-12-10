package org.bladerunnerjs.plugin.bundler.js;

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
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.bundler.AbstractBundlerPlugin;
import org.bladerunnerjs.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.plugin.bundlesource.js.NamespacedJsSourceModule;
import org.bladerunnerjs.plugin.taghandler.TagHandlerPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;
import org.bladerunnerjs.utility.JsStyleUtility;
import org.json.simple.JSONObject;

public class NamespacedJsBundlerPlugin extends AbstractBundlerPlugin implements BundlerPlugin, TagHandlerPlugin {
	public static final String JS_STYLE = "namespaced-js";
	
	private ContentPathParser contentPathParser;
	private List<String> prodRequestPaths = new ArrayList<>();
	private BRJS brjs;
	
	{
		try {
			ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
			contentPathParserBuilder
				.accepts("namespaced-js/bundle.js").as("bundle-request")
					.and("namespaced-js/module<module>.js").as("single-module-request")
					.and("namespaced-js/package-definitions.js").as("package-definitions-request")
				.where("module").hasForm(".+"); // TODO: ensure we really need such a simple hasForm() -- we didn't use to need it
			
			contentPathParser = contentPathParserBuilder.build();
			prodRequestPaths.add(contentPathParser.createRequest("bundle-request"));
		}
		catch(MalformedTokenException e) {
			throw new RuntimeException(e);
		}
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
		return "namespaced-js";
	}
	
	@Override
	public String getMimeType() {
		return "text/javascript";
	}
	
	@Override
	public ContentPathParser getContentPathParser() {
		return contentPathParser;
	}
	
	@Override
	public List<String> getValidDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		List<String> requestPaths = new ArrayList<>();
		
		try {
			requestPaths.add(contentPathParser.createRequest("package-definitions-request"));
			for(SourceModule sourceModule : bundleSet.getSourceModules()) {
				if(sourceModule instanceof NamespacedJsSourceModule) {
					requestPaths.add(contentPathParser.createRequest("single-module-request", sourceModule.getRequirePath()));
				}
			}
		}
		catch(MalformedTokenException e) {
			throw new BundlerProcessingException(e);
		}
		
		return requestPaths;
	}
	
	@Override
	public List<String> getValidProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		return prodRequestPaths;
	}
	
	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException {
		try {
			if(contentPath.formName.equals("single-module-request")) {
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding())) {
					SourceModule jsModule = bundleSet.getBundlableNode().getSourceModule(contentPath.properties.get("module"));
					writer.write( globalizeNonNamespacedJsClasses(jsModule, new ArrayList<SourceModule>()) );
					IOUtils.copy(jsModule.getReader(), writer);
				}
			}
			else if(contentPath.formName.equals("bundle-request")) {
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding())) {
								
					Map<String, Map<String, ?>> packageStructure = createPackageStructureForCaplinJsClasses(bundleSet, writer);
    				writePackageStructure(packageStructure, writer);
    				writer.write("\n");
					
    				List<SourceModule> processedSourceModules = new ArrayList<SourceModule>();
    				
					for(SourceModule sourceModule : bundleSet.getSourceModules()) {
						if(sourceModule instanceof NamespacedJsSourceModule)
						{
							writer.write( globalizeNonNamespacedJsClasses(sourceModule, processedSourceModules) );
    						writer.write("// " + sourceModule.getRequirePath() + "\n");
    						IOUtils.copy(sourceModule.getReader(), writer);
    						writer.write("\n\n");
						}
					}
				}
			}
			else if(contentPath.formName.equals("package-definitions-request")) {
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding())) {
    				Map<String, Map<String, ?>> packageStructure = createPackageStructureForCaplinJsClasses(bundleSet, writer);
    				writePackageStructure(packageStructure, writer);
				}
			}
			else {
				throw new BundlerProcessingException("unknown request form '" + contentPath.formName + "'.");
			}
		}
		catch(ModelOperationException | ConfigException | IOException | RequirePathException e) {
			throw new BundlerProcessingException(e);
		}
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation)
	{
		if ( !(assetLocation.getAssetContainer() instanceof JsLib) && JsStyleUtility.getJsStyle(assetLocation.dir()).equals(JS_STYLE)) {
			// TODO: blow up if the package of the assetLocation would not be a valid namespace
			
			return assetLocation.getAssetContainer().root().getAssetFilesWithExtension(assetLocation, NamespacedJsSourceModule.class, "js");
		}
		else {
			return Arrays.asList();
		}
	}
	
	@Override
	public List<LinkedAsset> getLinkedResourceFiles(AssetLocation assetLocation)
	{
		return Arrays.asList();
	}
	
	@Override
	public List<Asset> getResourceFiles(AssetLocation assetLocation)
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
		
		for(SourceModule sourceModule : bundleSet.getSourceModules()) {
			if(sourceModule instanceof NamespacedJsSourceModule) {
				List<String> packageList = Arrays.asList(sourceModule.getNamespacedName().split("\\."));
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
	
	private String globalizeNonNamespacedJsClasses(SourceModule sourceModule, List<SourceModule> globalizedModules) throws ModelOperationException {
		StringBuffer stringBuffer = new StringBuffer();
		
		for(SourceModule dependentSourceModule : sourceModule.getDependentSourceModules(null)) 
		{		
			if ( !(dependentSourceModule instanceof NamespacedJsSourceModule) && !globalizedModules.contains(dependentSourceModule) ) 
			{
 				if (dependentSourceModule.isEncapsulatedModule()) 
 				{
    				stringBuffer.append(dependentSourceModule.getNamespacedName() + " = require('" + dependentSourceModule.getRequirePath()  + "');\n");
    				globalizedModules.add(dependentSourceModule);
 				}
			}
		}
		
		return stringBuffer.toString();
	}
}
