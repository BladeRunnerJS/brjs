package org.bladerunnerjs.plugin.plugins.bundlers.i18n;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;

public class I18nAssetFile implements Asset
{
	
	public static final String I18N_REGEX = "([a-z]{2})(_([A-Z]{2}))?";
	public static final String I18N_PROPERTIES_FILE_REGEX = I18N_REGEX+"\\.properties";
	
	private Pattern i18nPropertiesPattern = Pattern.compile(I18N_PROPERTIES_FILE_REGEX);
	
	private AssetLocation assetLocation;
	private File assetFile;

	@Override
	public void initialize(AssetLocation assetLocation, File assetFileOrDir) throws AssetFileInstantationException
	{
		this.assetLocation = assetLocation;
		this.assetFile = assetFileOrDir;
	}

	@Override
	public Reader getReader() throws FileNotFoundException
	{
		return new FileReader(assetFile);
	}

	@Override
	public AssetLocation getAssetLocation()
	{
		return assetLocation;
	}

	@Override
	public String getAssetName()
	{
		return assetFile.getName();
	}

	@Override
	public String getAssetPath()
	{
		return assetFile.getAbsolutePath();
	}

	@Override
	public File getUnderlyingFile()
	{
		return assetFile;
	}
	
	public String getLocaleLanguage()
	{
		return getMatchedValueFromPropertiesPattern(1);
	}
	
	public String getLocaleLocation()
	{
		return getMatchedValueFromPropertiesPattern(3);
	}

	public Map<String,String> getLocaleProperties() throws IOException
	{
		Map<String, String> propertiesMap = new HashMap<String,String>();
		
		Properties i18nProperties = new Properties();
		i18nProperties.load( new FileReader(assetFile) );
		
		for (String property : i18nProperties.stringPropertyNames())
		{
			String value = i18nProperties.getProperty(property);
			propertiesMap.put(property, value);
		}

		return propertiesMap;
	}
	
	private String getMatchedValueFromPropertiesPattern(int groupNum)
	{
		Matcher m = i18nPropertiesPattern.matcher( getAssetName() );
		if (m.matches() && m.groupCount() >= groupNum)
		{
			return (m.group(groupNum) != null) ? m.group(groupNum) : "";
		}
		return "";
	}

}
