package org.bladerunnerjs.plugin.plugins.bundlers.thirdparty;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;

public class ThirdpartyTagHandlerPlugin extends AbstractTagHandlerPlugin {
	private ContentPlugin thirdpartyContentPlugin;
	
	@Override
	public void setBRJS(BRJS brjs) {
		thirdpartyContentPlugin = brjs.plugins().contentProvider("thirdparty");
	}

	@Override
	public String getGroupName() {
		return "text/javascript";
	}
	
	@Override
	public String getTagName()
	{
		return "thirdparty.bundle";
	}
	
	@Override
	public List<String> getPluginsThatMustAppearBeforeThisPlugin() {
		return new ArrayList<>();
	}
	
	@Override
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return new ArrayList<>();
	}
	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
		try {
			for (String requestPath : thirdpartyContentPlugin.getValidDevContentPaths(bundleSet, locale))
			{
				writer.write("<script type='text/javascript' src='" + requestPath + "'></script>\n");
			}
		}
		catch(ContentProcessingException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
		try {
			for (String requestPath : thirdpartyContentPlugin.getValidProdContentPaths(bundleSet, locale))
			{
				writer.write("<script type='text/javascript' src='" + requestPath + "'></script>\n");
			}
		}
		catch(ContentProcessingException e) {
			throw new IOException(e);
		}
	}
	
}
