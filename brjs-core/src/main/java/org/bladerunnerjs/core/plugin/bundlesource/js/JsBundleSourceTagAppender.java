package org.bladerunnerjs.core.plugin.bundlesource.js;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.TagAppender;
import org.json.simple.JSONObject;

public class JsBundleSourceTagAppender implements TagAppender {
	@Override
	public void writePreTagContent(List<String> bundlerRequestPaths, Writer writer) throws IOException {
		Map<String, Map<String, ?>> packageStructure = createPackageStructureForCaplinJsClasses(bundlerRequestPaths, writer);
		writePackageStructure(packageStructure, writer);
	}
	
	@Override
	public void writePostTagContent(List<String> bundlerRequestPaths, Writer writer) throws IOException {
		globalizeNonCaplinJsClasses(bundlerRequestPaths, writer);
	}
	
	private Map<String, Map<String, ?>> createPackageStructureForCaplinJsClasses(List<String> bundlerRequestPaths, Writer writer) {
		Map<String, Map<String, ?>> packageStructure = new HashMap<>();
		
		for(String bundlerRequestPath : bundlerRequestPaths) {
			// TODO: how do work this out?
			boolean isCaplinJsClass = false;
			
			if(isCaplinJsClass) {
				addPackageToStructure(packageStructure, bundlerRequestPath.split("/"));
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
	
	private void globalizeNonCaplinJsClasses(List<String> bundlerRequestPaths, Writer writer) throws IOException {
		for(String bundlerRequestPath : bundlerRequestPaths) {
			// TODO: how do work this out?
			boolean isCaplinJsClass = false;
			
			if(!isCaplinJsClass) {
				String className = bundlerRequestPath.replaceAll("/", ".");
				writer.write(className + " = require('" + bundlerRequestPath  + "')");
			}
		}
	}
}
