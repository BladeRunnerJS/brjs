package org.bladerunnerjs.plugin.plugins.bundlers.i18n;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.RequestMode;
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
	public void writeTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, RequestMode requestMode, Locale locale, Writer writer, String version) throws IOException
	{
		try
		{
			String contentPath = "";
			if (locale.isCompleteLocale()) {
				contentPath = i18nContentPlugin.getContentPathParser().createRequest(I18nContentPlugin.LANGUAGE_AND_LOCATION_BUNDLE, locale.getLanguageCode(), locale.getCountryCode());
			} else {
				contentPath = i18nContentPlugin.getContentPathParser().createRequest(I18nContentPlugin.LANGUAGE_BUNDLE, locale.getLanguageCode());				
			}
			App app = bundleSet.getBundlableNode().app();
			String requestPath = (requestMode == RequestMode.Dev) ? app.createDevBundleRequest(contentPath, version) : app.createProdBundleRequest(contentPath, version);
			
			writer.write("<script type=\"text/javascript\" src=\"" + requestPath + "\"></script>\n");
		}
		catch (Exception ex)
		{
			throw new IOException(ex);
		}
	}

}
