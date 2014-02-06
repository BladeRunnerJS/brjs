package org.bladerunnerjs.plugin.plugins.bundlers.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;


public class I18nTagHandlerPlugin extends AbstractTagHandlerPlugin
{

	private static final String LOCALE_COOKIE_NAME_KEY = "cookieName";
	private static final String DEFAULT_COOKIE_NAME = "brjsLocale";

	@Override
	public void setBRJS(BRJS brjs)
	{
	}
	
	@Override
	public String getTagName()
	{
		return "i18n.bundle";
	}

	@Override
	public String getGroupName()
	{
		return "";
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

	private void writeTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws ConfigException, IOException
	{
		App app = bundleSet.getBundlableNode().getApp();
		String[] supportedLocales = StringUtils.split( app.appConf().getLocales(), "," );
		String defaultLocale = supportedLocales[0];
		String localeCookieName = (tagAttributes.get(LOCALE_COOKIE_NAME_KEY) != null) ? tagAttributes.get(LOCALE_COOKIE_NAME_KEY) : DEFAULT_COOKIE_NAME;
		writeI18nScriptContent(localeCookieName, supportedLocales, defaultLocale, writer);
	}

	private void writeI18nScriptContent(String localeCookieName, String[] supportedLocales, String defaultLocale, Writer writer) throws IOException
	{
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("org/bladerunnerjs/plugin/plugins/bundlers/i18n/i18nScript.js");
		String scriptContentFromFile = IOUtils.toString(in);
		String scriptContent = String.format(scriptContentFromFile, localeCookieName, stringifyLocales(supportedLocales), defaultLocale);
		writer.write(scriptContent);
	}

	private String stringifyLocales(String[] supportedLocales)
	{
		String stringifiedLocales = "\"" + StringUtils.join(supportedLocales, "\",\"") + "\"";
		stringifiedLocales = stringifiedLocales.replaceAll(" ", "");
		return stringifiedLocales;
	}

}
