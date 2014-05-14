package org.bladerunnerjs.plugin.plugins.bundlers.i18n;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyContentPlugin;


public class I18nTagHandlerPlugin extends AbstractTagHandlerPlugin
{
	private I18nContentPlugin i18nContentPlugin;

	@Override
	public void setBRJS(BRJS brjs)
	{
		VirtualProxyContentPlugin virtualProxyContentPlugin = (VirtualProxyContentPlugin) brjs.plugins().contentProvider("i18n");
		i18nContentPlugin = (I18nContentPlugin) virtualProxyContentPlugin.getUnderlyingPlugin();
	}
	
	@Override
	public String getTagName()
	{
		return "i18n.bundle";
	}

	@Override
	public String getGroupName()
	{
		return "text/javascript";
	}
	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
		try
		{
			writeTagContent(tagAttributes, bundleSet, locale, writer);
		}
		catch (Exception ex)
		{
			throw new IOException(ex);
		}
	}

	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
		try
		{
			writeTagContent(tagAttributes, bundleSet, locale, writer);
		}
		catch (Exception ex)
		{
			throw new IOException(ex);
		}
	}

	private void writeTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws ConfigException, IOException, MalformedTokenException
	{
		String requestUrl = "";
		if (locale.contains("_")) {
			requestUrl = i18nContentPlugin.getContentPathParser().createRequest(I18nContentPlugin.LANGUAGE_AND_LOCATION_BUNDLE, StringUtils.substringBefore(locale, "_"), StringUtils.substringAfter(locale, "_"));			
		} else {
			requestUrl = i18nContentPlugin.getContentPathParser().createRequest(I18nContentPlugin.LANGUAGE_BUNDLE, locale);				
		}
		writer.write("<script type=\"text/javascript\" src=\""+requestUrl+"\"></script>\n");
	}

}
