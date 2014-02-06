package org.bladerunnerjs.plugin.plugins.bundlers.i18n;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.exception.RequirePathException;
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
	public List<String> getValidDevContentPaths(BundleSet bundleSet, String... locales) throws BundlerProcessingException
	{
		for (@SuppressWarnings("unused") String locale : locales)
		{
			
		}
		return Arrays.asList();
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, String... locales) throws BundlerProcessingException
	{
		return getValidDevContentPaths(bundleSet, locales);
	}
	
	private void generateBundleForLocale(BundleSet bundleSet, OutputStream os, String language, String location) throws BundlerProcessingException
	{
		Map<String,String> propertiesMap = new HashMap<String,String>();
		
		for (Asset asset : getOrderedI18nAssetFiles(bundleSet))
		{
			addI18nProperties(propertiesMap, language, location, (I18nAssetFile) asset);
		}

		writePropertiesMapToOutput(propertiesMap, os);
	}

	private void addI18nProperties(Map<String,String> propertiesMap, String language, String location, I18nAssetFile i18nFile) throws BundlerProcessingException
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
				throw new BundlerProcessingException(ex, "Error getting locale properties from file");
			}
		}
	}
	
	private void writePropertiesMapToOutput(Map<String, String> propertiesMap, OutputStream os)
	{
		PrintWriter writer = new PrintWriter(os);
		
		StringBuilder output = new StringBuilder();
		
		output.append("{"+NEWLINE);		
		for (String key : propertiesMap.keySet())
		{
			String value = propertiesMap.get(key);
			output.append(QUOTE+key+QUOTE+":"+QUOTE+value+QUOTE+","+NEWLINE);
		}
		if (propertiesMap.size() > 0)
		{
			output.deleteCharAt( output.length() - 2 ); /* delete the last comma */			
		}
		output.append("};");
		
		writer.write(output.toString());
		writer.flush();
	}
	
	private List<I18nAssetFile> getOrderedI18nAssetFiles(BundleSet bundleSet)
	{
		List<I18nAssetFile> languageOnlyAssets = new ArrayList<I18nAssetFile>();
		List<I18nAssetFile> languageAndLocationAssets = new ArrayList<I18nAssetFile>();
		
		List<Asset> propertyAssets = bundleSet.getResourceFiles("properties");
		
		List<I18nAssetFile> i18nAssets = new LinkedList<I18nAssetFile>();
		i18nAssets.addAll( getI18nPropertiesInAssetContainer(propertyAssets, Blade.class) );
		i18nAssets.addAll( getI18nPropertiesInAssetContainer(propertyAssets, Bladeset.class) );
		i18nAssets.addAll( getI18nPropertiesInAssetContainer(propertyAssets, Aspect.class) );
		i18nAssets.addAll( getI18nPropertiesInAssetContainer(propertyAssets, Workbench.class) );
		
		for (I18nAssetFile i18nAsset : i18nAssets)
		{
			if (i18nAsset.getLocaleLanguage().length() > 0 && i18nAsset.getLocaleLocation().length() > 0)
			{
				languageAndLocationAssets.add(i18nAsset);
			}
			else if (i18nAsset.getLocaleLanguage().length() > 0)
			{
				languageOnlyAssets.add(i18nAsset);					
			}
		}
		
		List<I18nAssetFile> orderedI18nAssets = new LinkedList<I18nAssetFile>();
		orderedI18nAssets.addAll(languageOnlyAssets);
		orderedI18nAssets.addAll(languageAndLocationAssets);
		
		return orderedI18nAssets;
	}
	
	
	private List<I18nAssetFile> getI18nPropertiesInAssetContainer(List<Asset> assets, Class<? extends AssetContainer> assetContainerType)
	{
		List<I18nAssetFile> i18nAssets = new ArrayList<I18nAssetFile>();
		
		for (Asset asset : assets)
		{
			if (asset instanceof I18nAssetFile)
			{
				I18nAssetFile i18nAssetFile = (I18nAssetFile) asset;
				AssetContainer i18nAssetContainer = i18nAssetFile.getAssetLocation().getAssetContainer();
				if (i18nAssetContainer.getClass() == assetContainerType)
				{
					i18nAssets.add(i18nAssetFile);
				}
			}
		}
		return i18nAssets;
	}
	
	
}
