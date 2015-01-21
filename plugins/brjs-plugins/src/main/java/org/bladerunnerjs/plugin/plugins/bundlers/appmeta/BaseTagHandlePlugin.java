package org.bladerunnerjs.plugin.plugins.bundlers.appmeta;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;


public class BaseTagHandlePlugin extends AbstractTagHandlerPlugin
{

	@Override
	public String getTagName()
	{
		return "base.tag";
	}

	@Override
	public void writeTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, RequestMode requestMode, Locale locale, Writer writer, String version) throws IOException
	{
		writer.write("<!-- base tag deprecated -->");
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
	}

}
