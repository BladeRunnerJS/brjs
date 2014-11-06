package org.bladerunnerjs.plugin.plugins.bundlers.i18n;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;


public class I18nTagHandlerPlugin extends AbstractTagHandlerPlugin
{
	private ContentPlugin i18nContentPlugin;

	@Override
	public void setBRJS(BRJS brjs)
	{
		i18nContentPlugin = brjs.plugins().contentPlugin("i18n");
	}
	
	@Override
	public String getTagName()
	{
		return "i18n.bundle";
	}
	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, Writer writer, String version) throws IOException
	{
		try
		{
			writeTagContent(true, tagAttributes, bundleSet, locale, writer, version);
		}
		catch (Exception ex)
		{
			throw new IOException(ex);
		}
	}

	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, Writer writer, String version) throws IOException
	{
		try
		{
			writeTagContent(false, tagAttributes, bundleSet, locale, writer, version);
		}
		catch (Exception ex)
		{
			throw new IOException(ex);
		}
	}

	private void writeTagContent(boolean isDev, Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, Writer writer, String version) throws ConfigException, IOException, MalformedTokenException
	{
		String contentPath = "";
		if (locale.isCompleteLocale()) {
			contentPath = i18nContentPlugin.getContentPathParser().createRequest(I18nContentPlugin.LANGUAGE_AND_LOCATION_BUNDLE, locale.getLanguageCode(), locale.getCountryCode());
		} else {
			contentPath = i18nContentPlugin.getContentPathParser().createRequest(I18nContentPlugin.LANGUAGE_BUNDLE, locale.getLanguageCode());				
		}
		App app = bundleSet.getBundlableNode().app();
		String requestPath = (isDev) ? app.createDevBundleRequest(contentPath, version) : app.createProdBundleRequest(contentPath, version);
		
		writer.write("<script type=\"text/javascript\" src=\"" + requestPath + "\"></script>\n");
	}

}
