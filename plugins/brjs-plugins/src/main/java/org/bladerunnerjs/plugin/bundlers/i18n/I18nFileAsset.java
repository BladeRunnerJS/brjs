package org.bladerunnerjs.plugin.bundlers.i18n;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.plugin.bundlers.aliasing.NamespaceException;
import org.bladerunnerjs.utility.UnicodeReader;

public class I18nFileAsset implements Asset
{
	
	class Messages {
		public static final String PROPERTY_NAMESPACE_EXCEPTION = "i18n property '%s' in property file '%s' is invalid. It must start with the same namespace as it's container, '%s'.";
	}
	
	private MemoizedFile assetFile;
	private String assetPath;
	private String defaultFileCharacterEncoding;
	private Locale locale;
	private AssetContainer assetContainer;
	
	public I18nFileAsset(MemoizedFile i18nFile, AssetContainer assetContainer, String requirePrefix)
	{
		this.assetContainer = assetContainer;
		this.assetFile = i18nFile;
		assetPath = assetContainer.app().dir().getRelativePath(assetFile);
		try
		{
			defaultFileCharacterEncoding = assetContainer.root().bladerunnerConf().getDefaultFileCharacterEncoding();
		}
		catch (ConfigException ex)
		{
			throw new RuntimeException(ex);
		}
		locale = Locale.createLocaleFromFilepath(getAssetName());
	}

	@Override
	public Reader getReader() throws IOException
	{
		return new UnicodeReader(assetFile, defaultFileCharacterEncoding);
	}
	
	@Override
	public MemoizedFile file()
	{
		return assetFile;
	}
	
	@Override
	public String getAssetName()
	{
		return assetFile.getName();
	}

	@Override
	public String getAssetPath()
	{
		return assetPath;
	}
	
	@Override
	public List<String> getRequirePaths() {
		// TODO: we should return the complete list of i18n tokens
		return Collections.emptyList();
	}
	
	@Override
	public String getPrimaryRequirePath() {
		return null;
	}

	public Map<String,String> getLocaleProperties() throws IOException, RequirePathException, NamespaceException
	{
		Map<String, String> propertiesMap = new HashMap<String,String>();
		Properties i18nProperties = new Properties();
		
		try(Reader propertiesReader = new UnicodeReader(assetFile, defaultFileCharacterEncoding)) {
			i18nProperties.load( propertiesReader );
			
			for (String property : i18nProperties.stringPropertyNames())
			{
				//TODO: fix me after mega commit
//				assetLocation().assertIdentifierCorrectlyNamespaced(property);
				String value = i18nProperties.getProperty(property);
				propertiesMap.put(property, value.replaceAll("\n", "\\\\n"));
			}
		}
		
		return propertiesMap;
	}
	
	public Locale getLocale() {
		return locale;
	}

	@Override
	public AssetContainer assetContainer()
	{
		return assetContainer;
	}

}
