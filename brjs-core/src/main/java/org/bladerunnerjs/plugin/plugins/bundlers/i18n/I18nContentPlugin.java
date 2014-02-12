package org.bladerunnerjs.plugin.plugins.bundlers.i18n;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class I18nContentPlugin extends AbstractContentPlugin
{
	public static final String LANGUAGE_BUNDLE = "language-bundle";
	public static final String LANGUAGE_AND_LOCATION_BUNDLE = "language-and-location-bundle";
	private static final String LANGUAGE_PROPERTY_NAME = "language";
	private static final String LOCATION_PROPERTY_NAME = "location";
	private static final String NEWLINE = "\n";
	private static final String QUOTE = "\"";
	
	private ContentPathParser contentPathParser;
	
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder
			.accepts("i18n/<language>.js").as(LANGUAGE_BUNDLE)
			.and("i18n/<language>_<location>.js").as(LANGUAGE_AND_LOCATION_BUNDLE)
			.where(LANGUAGE_PROPERTY_NAME).hasForm("[a-z]{2}")
			.and(LOCATION_PROPERTY_NAME).hasForm("[A-Z]{2}");
		
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
	public String getGroupName()
	{
		return "text/javascript";
	}

	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws ContentProcessingException
	{
		if (contentPath.formName.equals(LANGUAGE_BUNDLE)) 
		{
			generateBundleForLocale(bundleSet, os, contentPath.properties.get(LANGUAGE_PROPERTY_NAME), "");
		}
		else if (contentPath.formName.equals(LANGUAGE_AND_LOCATION_BUNDLE)) 
		{
			generateBundleForLocale(bundleSet, os, contentPath.properties.get(LANGUAGE_PROPERTY_NAME), contentPath.properties.get(LOCATION_PROPERTY_NAME));
		} 
		else
		{
			throw new ContentProcessingException("unknown request form '" + contentPath.formName + "'.");
		}
	}

	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException
	{
		try 
		{
			List<String> contentPaths = new ArrayList<String>();
			for (String locale : locales)
			{
				String requestPath = "";
				if (locale.contains("_")) {
					requestPath = getContentPathParser().createRequest(I18nContentPlugin.LANGUAGE_AND_LOCATION_BUNDLE, StringUtils.substringBefore(locale, "_"), StringUtils.substringAfter(locale, "_"));			
				} else {
					requestPath = getContentPathParser().createRequest(I18nContentPlugin.LANGUAGE_BUNDLE, locale);				
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

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException
	{
		return getValidDevContentPaths(bundleSet, locales);
	}
	
	private void generateBundleForLocale(BundleSet bundleSet, OutputStream os, String language, String location) throws ContentProcessingException
	{
		Map<String,String> propertiesMap = new HashMap<String,String>();
		
		for (Asset asset : getI18nAssetFiles(bundleSet))
		{
			addI18nProperties(propertiesMap, language, location, (I18nAssetFile) asset);
		}

		writePropertiesMapToOutput(propertiesMap, os);
	}

	private void addI18nProperties(Map<String,String> propertiesMap, String language, String location, I18nAssetFile i18nFile) throws ContentProcessingException
	{
		if ( i18nFile.getLocaleLanguage().equals(language) && 
				(i18nFile.getLocaleLocation().equals("") || i18nFile.getLocaleLocation().equals(location)) )
		{
			try
			{
				propertiesMap.putAll( i18nFile.getLocaleProperties() );
			}
			catch (IOException | RequirePathException | NamespaceException ex)
			{
				throw new ContentProcessingException(ex, "Error getting locale properties from file");
			}
		}
	}
	
	private void writePropertiesMapToOutput(Map<String, String> propertiesMap, OutputStream os)
	{
		PrintWriter writer = new PrintWriter(os);
		
		StringBuilder output = new StringBuilder();
		
		output.append("window._brjsI18nProperties = [{"+NEWLINE);
		for (String key : propertiesMap.keySet())
		{
			String value = propertiesMap.get(key);
			output.append(QUOTE+key+QUOTE+":"+QUOTE+value+QUOTE+","+NEWLINE);
		}
		if (propertiesMap.size() > 0)
		{
			output.deleteCharAt( output.length() - 2 ); /* delete the last comma */			
		}
		output.append("}];");
		
		writer.write(output.toString());
		writer.flush();
	}
	
	private List<I18nAssetFile> getI18nAssetFiles(BundleSet bundleSet)
	{
		List<I18nAssetFile> languageOnlyAssets = new ArrayList<I18nAssetFile>();
		List<I18nAssetFile> languageAndLocationAssets = new ArrayList<I18nAssetFile>();
		
		List<Asset> propertyAssets = bundleSet.getResourceFiles("properties");
		
		for (Asset asset : propertyAssets)
		{
			if (asset instanceof I18nAssetFile)
			{
				I18nAssetFile i18nAsset = (I18nAssetFile) asset;
				if (i18nAsset.getLocaleLanguage().length() > 0 && i18nAsset.getLocaleLocation().length() > 0)
				{
					languageAndLocationAssets.add(i18nAsset);
				}
				else if (i18nAsset.getLocaleLanguage().length() > 0)
				{
					languageOnlyAssets.add(i18nAsset);
				}
			}
		}
		
		List<I18nAssetFile> orderedI18nAssets = new LinkedList<I18nAssetFile>();
		orderedI18nAssets.addAll(languageOnlyAssets);
		orderedI18nAssets.addAll(languageAndLocationAssets);
		
		return orderedI18nAssets;
	}
	
	
}
