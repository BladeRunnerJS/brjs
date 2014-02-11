package org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;
import org.json.simple.JSONObject;

public class NamespacedJsContentPlugin extends AbstractContentPlugin {

	public static final String JS_STYLE = "namespaced-js";
	
	private ContentPathParser contentPathParser;
	private List<String> prodRequestPaths = new ArrayList<>();
	private BRJS brjs;
	
	{
		try {
			ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
			contentPathParserBuilder
				.accepts("namespaced-js/bundle.js").as("bundle-request")
					.and("namespaced-js/module/<module>.js").as("single-module-request")
					.and("namespaced-js/package-definitions.js").as("package-definitions-request")
				.where("module").hasForm(ContentPathParserBuilder.PATH_TOKEN);
			
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
	public String getRequestPrefix() {
		return "namespaced-js";
	}

	@Override
	public String getGroupName() {
		return "text/javascript";
	}
	
	@Override
	public ContentPathParser getContentPathParser() {
		return contentPathParser;
	}
	
	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException {
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
			throw new ContentProcessingException(e);
		}
		
		return requestPaths;
	}
	
	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException {
		return prodRequestPaths;
	}
	
	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws ContentProcessingException {
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
								
					StringWriter jsContent = new StringWriter();

					// do this first and buffer the content so we know which modules have been globally namespaced
					List<SourceModule> processedGlobalizedSourceModules = new ArrayList<SourceModule>();
					for(SourceModule sourceModule : bundleSet.getSourceModules()) {
						if(sourceModule instanceof NamespacedJsSourceModule)
						{
							jsContent.write( globalizeNonNamespacedJsClasses(sourceModule, processedGlobalizedSourceModules) );
							jsContent.write("// " + sourceModule.getRequirePath() + "\n");
							IOUtils.copy(sourceModule.getReader(), jsContent);
							jsContent.write("\n\n");
						}
					}
					
					Map<String, Map<String, ?>> packageStructure = createPackageStructureForCaplinJsClasses(bundleSet, processedGlobalizedSourceModules, writer);
					writePackageStructure(packageStructure, writer);
					writer.write("\n");
					
					writer.write( jsContent.toString() );
				}
			}
			else if(contentPath.formName.equals("package-definitions-request")) {
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding())) {
					
					List<SourceModule> processedGlobalizedSourceModules = new ArrayList<SourceModule>();
					for(SourceModule sourceModule : bundleSet.getSourceModules()) {
						if(sourceModule instanceof NamespacedJsSourceModule)
						{
							globalizeNonNamespacedJsClasses(sourceModule, processedGlobalizedSourceModules);
						}
					}
					
					Map<String, Map<String, ?>> packageStructure = createPackageStructureForCaplinJsClasses(bundleSet, processedGlobalizedSourceModules, writer);
					writePackageStructure(packageStructure, writer);
				}
			}
			else {
				throw new ContentProcessingException("unknown request form '" + contentPath.formName + "'.");
			}
		}
		catch(ModelOperationException | ConfigException | IOException | RequirePathException e) {
			throw new ContentProcessingException(e);
		}
	}
	
	private Map<String, Map<String, ?>> createPackageStructureForCaplinJsClasses(BundleSet bundleSet, List<SourceModule> globalizedModules, Writer writer) {
		Map<String, Map<String, ?>> packageStructure = new HashMap<>();
		
		for(SourceModule sourceModule : bundleSet.getSourceModules()) {
			if (sourceModule instanceof NamespacedJsSourceModule)
			{
				List<String> packageList = Arrays.asList(sourceModule.getClassname().split("\\."));
				addPackageToStructure(packageStructure, packageList.subList(0, packageList.size() - 1));
			}
		}
		
		for(SourceModule sourceModule : globalizedModules) {
			String namespacedName = sourceModule.getRequirePath().replace('/', '.');
			namespacedName = (namespacedName.startsWith(".")) ? StringUtils.substringAfter(namespacedName, ".") : namespacedName;
			List<String> packageList = Arrays.asList(namespacedName.split("\\."));
			addPackageToStructure(packageStructure, packageList.subList(0, packageList.size() - 1));
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
	
	private String globalizeNonNamespacedJsClasses(SourceModule sourceModule, List<SourceModule> globalizedModules) throws ModelOperationException, RequirePathException {
		StringBuffer stringBuffer = new StringBuffer();
		
		for(SourceModule dependentSourceModule : sourceModule.getDependentSourceModules(null)) 
		{
			if (dependentSourceModule.isEncapsulatedModule() && !globalizedModules.contains(dependentSourceModule) ) 
			{
   				stringBuffer.append(dependentSourceModule.getClassname() + " = require('" + dependentSourceModule.getRequirePath()  + "');\n");
   				globalizedModules.add(dependentSourceModule);
			}
		}
		
		return stringBuffer.toString();
	}
	
}
