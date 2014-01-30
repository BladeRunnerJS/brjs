package org.bladerunnerjs.plugin.plugins.bundlers.i18n;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;


public class I18nTagHandlerPlugin extends AbstractTagHandlerPlugin
{

	@Override
	public String getTagName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGroupName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setBRJS(BRJS brjs)
	{
		// TODO Auto-generated method stub

	}

}
