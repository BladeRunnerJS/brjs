package org.bladerunnerjs.plugin.plugins.bundlers.i18n;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.thirdparty.ThirdpartyContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class I18nContentPlugin extends AbstractContentPlugin
{
	public static final String LANGUAGE_BUNDLE = "language-bundle";
	public static final String LANGUAGE_AND_LOCATION_BUNDLE = "language-and-location-bundle";
	private static final String LANGUAGE_PROPERTY_NAME = "language";
	private static final String LOCATION_PROPERTY_NAME = "location";
	private AssetPlugin i18nAssetPlugin = null;
	
	private ContentPathParser contentPathParser;
	private BRJS brjs;
	
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
		this.brjs = brjs;
		i18nAssetPlugin = brjs.plugins().assetPlugin(I18nAssetPlugin.class);
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
	public String getCompositeGroupName()
	{
		return null;
	}
	
	@Override
	public List<String> getPluginsThatMustAppearBeforeThisPlugin() {
		return Arrays.asList(ThirdpartyContentPlugin.class.getCanonicalName());
	}
	
	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os, String version) throws ContentProcessingException
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
		SortedMap<String,String> propertiesMap = new TreeMap<String,String>();
		
		for (Asset asset : getI18nAssetFiles(bundleSet))
		{
			addI18nProperties(propertiesMap, language, location, (I18nFileAsset) asset);
		}

		writePropertiesMapToOutput(propertiesMap, os);
	}

	private void addI18nProperties(Map<String,String> propertiesMap, String language, String location, I18nFileAsset i18nFile) throws ContentProcessingException
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
	
	private void writePropertiesMapToOutput(Map<String, String> propertiesMap, OutputStream os) throws ContentProcessingException
	{
		try(Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getBrowserCharacterEncoding())) {
			StringBuilder output = new StringBuilder();
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String jsonProperties = gson.toJson(propertiesMap);
			/* Replace doubly escaped newlines - GSON does the right thing and escapes newlines twice 
			 * since otherwise when they are decoded from JSON they become literal newlines. 
			 * Since thats actually what we want we undo the double escaping here. 
			 */
			jsonProperties = jsonProperties.replace("\\\\n", "\\n").replace("\\\\r", "\\r");
			output.append("window._brjsI18nProperties = [" + jsonProperties + "];");
			
			writer.write(output.toString());
			writer.flush();
		}
		catch (IOException | ConfigException e) {
			throw new ContentProcessingException(e);
		}
	}
	
	private List<I18nFileAsset> getI18nAssetFiles(BundleSet bundleSet)
	{
		List<I18nFileAsset> languageOnlyAssets = new ArrayList<I18nFileAsset>();
		List<I18nFileAsset> languageAndLocationAssets = new ArrayList<I18nFileAsset>();
		
		List<Asset> propertyAssets = bundleSet.getResourceFiles(i18nAssetPlugin);
		
		for (Asset asset : propertyAssets)
		{
			if (asset instanceof I18nFileAsset)
			{
				I18nFileAsset i18nAsset = (I18nFileAsset) asset;
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
		
		List<I18nFileAsset> orderedI18nAssets = new LinkedList<I18nFileAsset>();
		orderedI18nAssets.addAll(languageOnlyAssets);
		orderedI18nAssets.addAll(languageAndLocationAssets);
		
		return orderedI18nAssets;
	}
	
	
}
