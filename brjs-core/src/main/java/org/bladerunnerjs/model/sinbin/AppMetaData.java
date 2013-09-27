package org.bladerunnerjs.model.sinbin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.Theme;
import org.bladerunnerjs.model.sinbin.CutlassConfig;

public class AppMetaData
{
	private static final String CSS_EXT = ".css";
	
	private final Set<String> applicationThemes = new HashSet<>();
	private final List<File> applicationImages = new ArrayList<>();
	private final List<String> applicationLocales = new ArrayList<>();
	private final List<String> applicationBrowsers = new ArrayList<>();
	private final List<String> applicationLanguages = new ArrayList<>();
	
	private File applicationDirectory;
	
	public AppMetaData(App app)
	{
		applicationDirectory = app.dir();
		
		for(Aspect aspect : app.aspects()) {
			processI18nResources(aspect.file("resources/i18n"));
			processThemes(aspect.themes());
		}
		
		for(Bladeset bladeset : app.bladesets()) {
			processI18nResources(bladeset.file("resources/i18n"));
			processThemes(bladeset.themes());
			
			for(Blade blade : bladeset.blades()) {
				processI18nResources(blade.file("resources/i18n"));
				processThemes(blade.themes());
			}
		}
	}
	
	public List<String> getThemes()
	{
		List<String> themeList = new ArrayList<>();
		themeList.addAll(applicationThemes);
		
		return themeList;
	}

	public List<String> getBrowsers()
	{
		return applicationBrowsers;
	}

	public List<String> getLocales()
	{
		return applicationLocales;
	}

	public List<File> getImages()
	{
		return applicationImages;
	}
	
	public List<String> getLanguages()
	{
		return applicationLanguages;
	}
	
	public File getApplicationDirectory()
	{
		return applicationDirectory;
	}
	
	private void processI18nResources(File i18nResources) {
		if(i18nResources.exists()) {
			// TODO: this code is a bug since we got rid of the requirement of having langauge directories, and will change again
			// once we no longer have have bundler specific resource methods
			extractAllApplicationMetaDataFromResourcesI18nDirectory(i18nResources);
		}
	}
	
	private void processThemes(List<Theme> themes) {
		for(Theme theme : themes) {
			applicationThemes.add(theme.getName());
			extractLocalesBrowsersAndImages(theme.dir());
		}
	}
	
	private void extractAllApplicationMetaDataFromResourcesI18nDirectory(File resourcesI18nDirectory)
	{
		File[] i18nLanguages = resourcesI18nDirectory.listFiles();

		for (File i18nLanguageDirectory : i18nLanguages)
		{
			if(i18nLanguageDirectory.isDirectory() && !i18nLanguageDirectory.isHidden())
			{
				extractLanguageAndLocalesFromI18nPropertiesFiles(i18nLanguageDirectory);
			}
		}
	}
	
	private void extractLanguageAndLocalesFromI18nPropertiesFiles(File i18nLanguageDirectory)
	{
		if(!applicationLanguages.contains(i18nLanguageDirectory.getName()))
		{
			applicationLanguages.add(i18nLanguageDirectory.getName());
		}
		
		File[] i18nLocaleAndLanguagesPropertyFiles = i18nLanguageDirectory.listFiles();
		
		for (File i18nLocalesOrLanguagePropertyFile : i18nLocaleAndLanguagesPropertyFiles)
		{
			String i18nLocaleOrLanguage = i18nLocalesOrLanguagePropertyFile.getName().replace(".properties", "");
			
			if (i18nLocaleOrLanguage.matches("[a-zA-Z]{2}_[a-zA-Z]{2}") && !applicationLocales.contains(i18nLocaleOrLanguage))
			{
				applicationLocales.add(i18nLocaleOrLanguage);
			}
		}
		
	}
	
	private void extractLocalesBrowsersAndImages(File themesFolder)
	{
		for (File themeFile : themesFolder.listFiles())
		{
			String themeFileName = themeFile.getName();
			
			extractImageFileFromThemeFile(themeFileName, themeFile);
			extractLocalesAndBrowsersFromThemeFileName(themeFileName);

			if (themeFile.isDirectory() && !themeFile.isHidden())
			{
				extractLocalesBrowsersAndImages(themeFile);
			}
		}
	}
	
	private void extractLocalesAndBrowsersFromThemeFileName(String themeFileName)
	{
		if (themeFileName.matches(".*_[a-zA-Z]{2}_[a-zA-Z]{2}\\.css"))
		{
			extractApplicationLocale(themeFileName);
		}
		else if (themeFileName.matches(".*_.*\\.css"))
		{
			extractApplicationBrowser(themeFileName);
		}
	}
	
	private void extractImageFileFromThemeFile(String themeFileName, File themeFile)
	{
		String themeFileNameLowercase = themeFileName.toLowerCase();
		for (String imgExtension : CutlassConfig.IMAGE_EXTENSIONS)
		{
			if (themeFileNameLowercase.endsWith(imgExtension))
			{
				applicationImages.add(themeFile);
			}
		}
	}
	
	private void extractApplicationLocale(String themeFileNameWithLocale)
	{
		String locale = themeFileNameWithLocale.substring(themeFileNameWithLocale.length() - 9).replace(CSS_EXT, "");

		if (!applicationLocales.contains(locale))
		{
			applicationLocales.add(locale);
		}
	}
	
	private void extractApplicationBrowser(String themeFileNameWithLocale)
	{
		String browser = themeFileNameWithLocale.substring(themeFileNameWithLocale.lastIndexOf("_") + 1).replace(CSS_EXT, "");

		if (!applicationBrowsers.contains(browser))
		{
			applicationBrowsers.add(browser);
		}
	}
}
