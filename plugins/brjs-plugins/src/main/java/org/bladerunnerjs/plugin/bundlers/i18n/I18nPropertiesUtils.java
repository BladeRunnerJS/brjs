package org.bladerunnerjs.plugin.bundlers.i18n;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.NamespaceException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.plugin.Locale;


public class I18nPropertiesUtils
{

	public static Map<String,String> getI18nProperties(BundleSet bundleset, Locale locale) throws ContentProcessingException {
		Map<String,String> propertiesMap = new TreeMap<String,String>();
		
		for (I18nFileAsset asset : getI18nAssetFiles(bundleset))
		{
			addI18nProperties(propertiesMap, locale, asset);
		}
		
		return propertiesMap;
	}
	
	private static void addI18nProperties(Map<String,String> propertiesMap, Locale locale, I18nFileAsset i18nFile) throws ContentProcessingException
	{
		if (locale.isAbsoluteOrPartialMatch(i18nFile.getLocale())) {
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
	
	private static List<I18nFileAsset> getI18nAssetFiles(BundleSet bundleSet)
	{
		List<I18nFileAsset> languageOnlyAssets = new ArrayList<I18nFileAsset>();
		List<I18nFileAsset> languageAndLocationAssets = new ArrayList<I18nFileAsset>();
		
		for (Asset asset : bundleSet.getAssets("i18n!"))
		{
			if (asset instanceof I18nFileAsset)
			{
				I18nFileAsset i18nAsset = (I18nFileAsset) asset;
				Locale assetLocale = i18nAsset.getLocale();
				if (assetLocale.isCompleteLocale())
				{
					languageAndLocationAssets.add(i18nAsset);
				}
				else if (!assetLocale.isEmptyLocale())
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
