package org.bladerunnerjs.plugin.plugins.bundlers.appversion;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;


public class BundlePathTagHandlerPlugin extends AbstractTagHandlerPlugin
{

	@Override
	public String getTagName()
	{
		return "bundle.path";
	}

	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer, String version) throws IOException
	{
		writeProdTagContent(tagAttributes, bundleSet, locale, writer, version);
	}

	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer, String version) throws IOException
	{
		writer.write("../v/"+version);		
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
	}
	
}
