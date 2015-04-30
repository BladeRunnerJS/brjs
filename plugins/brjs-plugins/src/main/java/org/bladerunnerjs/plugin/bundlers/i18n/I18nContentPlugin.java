package org.bladerunnerjs.plugin.bundlers.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.plugin.CharResponseContent;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.RoutableContentPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class I18nContentPlugin extends AbstractContentPlugin implements RoutableContentPlugin
{
	public static final String LANGUAGE_BUNDLE = "language-bundle";
	public static final String LANGUAGE_AND_LOCATION_BUNDLE = "language-and-location-bundle";
	private static final String LANGUAGE_PROPERTY_NAME = "language";
	private static final String COUNTRY_PROPERTY_NAME = "country";
	
	private ContentPathParser contentPathParser;
	
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder
			.accepts("i18n/<language>.js").as(LANGUAGE_BUNDLE)
				.and("i18n/<language>_<country>.js").as(LANGUAGE_AND_LOCATION_BUNDLE)
			.where(LANGUAGE_PROPERTY_NAME).hasForm(Locale.LANGUAGE_CODE_FORMAT)
				.and(COUNTRY_PROPERTY_NAME).hasForm(Locale.COUNTRY_CODE_FORMAT);
		
		contentPathParser = contentPathParserBuilder.build();
	}
	
	
	@Override
	public void setBRJS(BRJS brjs)
	{
	}

	@Override
	public void close()
	{
	}
	
	@Override
	public String getRequestPrefix()
	{
		return "i18n";
	}
	
	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}
	
	@Override
	public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ContentProcessingException
	{
		ParsedContentPath parsedContentPath = contentPathParser.parse(contentPath);
		Locale locale = new Locale(parsedContentPath.properties.get(LANGUAGE_PROPERTY_NAME), parsedContentPath.properties.get(COUNTRY_PROPERTY_NAME));
		if (parsedContentPath.formName.equals(LANGUAGE_BUNDLE)) 
		{
			return generateBundleForLocale(bundleSet, locale);
		}
		else if (parsedContentPath.formName.equals(LANGUAGE_AND_LOCATION_BUNDLE)) 
		{
			return generateBundleForLocale(bundleSet, locale);
		} 
		else
		{
			throw new ContentProcessingException("unknown request form '" + parsedContentPath.formName + "'.");
		}
	}

	@Override
	public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException
	{
		try 
		{
			List<String> contentPaths = new ArrayList<String>();
			for (Locale locale : locales)
			{
				String requestPath = "";
				if (locale.isCompleteLocale()) {
					requestPath = getContentPathParser().createRequest(I18nContentPlugin.LANGUAGE_AND_LOCATION_BUNDLE, locale.getLanguageCode(), locale.getCountryCode());			
				} else {
					requestPath = getContentPathParser().createRequest(I18nContentPlugin.LANGUAGE_BUNDLE, locale.getLanguageCode());				
				}
				contentPaths.add(requestPath);
			}
			return contentPaths;
		}
		catch (Exception ex)
		{
			throw new ContentProcessingException(ex);
		}
	}
	
	private ResponseContent generateBundleForLocale(BundleSet bundleSet, Locale locale) throws ContentProcessingException
	{
		Map<String,String> propertiesMap = I18nPropertiesUtils.getI18nProperties(bundleSet, locale);

		return getReaderForProperties(bundleSet.bundlableNode().root(), locale, propertiesMap);
	}
	
	private ResponseContent getReaderForProperties(BRJS brjs, Locale locale, Map<String, String> propertiesMap) throws ContentProcessingException
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonProperties = gson.toJson(propertiesMap);
		/* Replace doubly escaped newlines - GSON does the right thing and escapes newlines twice 
		 * since otherwise when they are decoded from JSON they become literal newlines. 
		 * Since thats actually what we want we undo the double escaping here. 
		 */
		jsonProperties = jsonProperties.replace("\\\\n", "\\n").replace("\\\\r", "\\r");
		
		return new CharResponseContent( brjs, "if (!window._brjsI18nProperties) { window._brjsI18nProperties = {} };\n"
				+ "window._brjsI18nProperties['" + locale + "'] = " + jsonProperties + ";\n"
						+ "window._brjsI18nUseLocale = '" + locale + "';");
	}	
	
}
