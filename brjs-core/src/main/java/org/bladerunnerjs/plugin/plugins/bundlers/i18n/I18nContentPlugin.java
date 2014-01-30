package org.bladerunnerjs.plugin.plugins.bundlers.i18n;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class I18nContentPlugin extends AbstractContentPlugin
{
	private static final String LANGUAGE_BUNDLE = "language-bundle";
	private static final String LANGUAGE_AND_LOCATION_BUNDLE = "language-and-location-bundle";
	private static final String LANGUAGE_PROPERTY_NAME = "language";
	private static final String LOCATION_PROPERTY_NAME = "location";
	private static final String NEWLINE = "\n";
	private static final String QUOTE = "\"";
	
	private ContentPathParser contentPathParser;
	
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder
			.accepts("i18n/<language>.json").as(LANGUAGE_BUNDLE)
			.and("i18n/<language>_<location>.json").as(LANGUAGE_AND_LOCATION_BUNDLE)
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
		return null;
	}

	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException
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
			throw new BundlerProcessingException("unknown request form '" + contentPath.formName + "'.");
		}
	}

	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, List<String> locales) throws BundlerProcessingException
	{
		for (String locale : locales)
		{
			
		}
		return Arrays.asList();
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, List<String> locales) throws BundlerProcessingException
	{
		return getValidDevContentPaths(bundleSet, locales);
	}
	
	private void generateBundleForLocale(BundleSet bundleSet, OutputStream os, String language, String location) throws BundlerProcessingException
	{
		Map<String,String> propertiesMap = new HashMap<String,String>();
		
		for (Asset asset : bundleSet.getResourceFiles("properties"))
		{
			if (asset instanceof I18nAssetFile)
			{
				addI18nProperties(propertiesMap, language, location, (I18nAssetFile) asset);
			}
		}
		
		writePropertiesMapToOutput(propertiesMap, os);
	}

	private void addI18nProperties(Map<String,String> propertiesMap, String language, String location, I18nAssetFile i18nFile) throws BundlerProcessingException
	{
		if (i18nFile.getLocaleLanguage().equals(language) && i18nFile.getLocaleLocation().equals(location))
		{
			try
			{
				propertiesMap.putAll( i18nFile.getLocaleProperties() );
			}
			catch (IOException ex)
			{
				throw new BundlerProcessingException(ex, "Error getting locale properties from file");
			}
		}
	}
	
	private void writePropertiesMapToOutput(Map<String, String> propertiesMap, OutputStream os)
	{
		PrintWriter writer = new PrintWriter(os);
		writer.write("{"+NEWLINE);
		
		for (String key : propertiesMap.keySet())
		{
			String value = propertiesMap.get(key);
			writer.write(QUOTE+key+QUOTE+":"+QUOTE+value+QUOTE+NEWLINE);
		}
		
		writer.write("};");
		writer.flush();
	}
}
