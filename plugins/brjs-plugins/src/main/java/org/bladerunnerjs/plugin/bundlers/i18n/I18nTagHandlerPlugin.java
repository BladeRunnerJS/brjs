package org.bladerunnerjs.plugin.bundlers.i18n;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.plugin.ContentPlugin;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.RoutableContentPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractTagHandlerPlugin;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.utility.ContentPathParser;


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
			ContentPathParser i18nContentPathParser = i18nContentPlugin.castTo(RoutableContentPlugin.class).getContentPathParser();
			String contentPath = "";
			
			if (locale.isCompleteLocale()) {
				contentPath = i18nContentPathParser.createRequest(I18nContentPlugin.LANGUAGE_AND_LOCATION_BUNDLE, locale.getLanguageCode(), locale.getCountryCode());
			} else {
				contentPath = i18nContentPathParser.createRequest(I18nContentPlugin.LANGUAGE_BUNDLE, locale.getLanguageCode());				
			}
			App app = bundleSet.bundlableNode().app();
			String requestPath = app.requestHandler().createRelativeBundleRequest(contentPath, version);
			
			writer.write("<script type=\"text/javascript\" src=\"" + requestPath + "\"></script>\n");
		}
		catch (Exception ex)
		{
			throw new IOException(ex);
		}
	}

}
