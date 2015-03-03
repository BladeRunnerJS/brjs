package org.bladerunnerjs.plugin.bundlers.appmeta;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.base.AbstractTagHandlerPlugin;
import org.bladerunnerjs.model.RequestMode;


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
