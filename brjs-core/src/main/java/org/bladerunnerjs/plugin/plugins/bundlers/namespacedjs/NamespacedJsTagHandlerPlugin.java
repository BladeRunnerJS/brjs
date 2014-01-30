package org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;

public class NamespacedJsTagHandlerPlugin extends AbstractTagHandlerPlugin {
	private ContentPlugin namespacedJsTagHandlerPlugin;
	
	@Override
	public void setBRJS(BRJS brjs) {
		namespacedJsTagHandlerPlugin = brjs.plugins().contentProvider("namespaced-js");
	}

	@Override
	public String getGroupName() {
		return "text/javascript";
	}
	
	@Override
	public String getTagName() {
		return "namespaced-js";
	}
	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		List<String> locales = new ArrayList<>();
		locales.add(locale);
		
		try {
			writeTagContent(bundleSet, namespacedJsTagHandlerPlugin.getValidDevContentPaths(bundleSet, locales), writer);
		}
		catch (BundlerProcessingException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		List<String> locales = new ArrayList<>();
		locales.add(locale);
		
		try {
			writeTagContent(bundleSet, namespacedJsTagHandlerPlugin.getValidProdContentPaths(bundleSet, locales), writer);
		}
		catch (BundlerProcessingException e) {
			throw new IOException(e);
		}
	}
	
	private void writeTagContent(BundleSet bundleSet, List<String> requestPaths, Writer writer) throws IOException {
		for(String bundlerRequestPath : requestPaths) {
			writer.write("<script type='text/javascript' src='" + bundlerRequestPath + "'></script>\n");
		}
	}
	
	@Override
	public int priority()
	{
		return 20;
	}
	
}
