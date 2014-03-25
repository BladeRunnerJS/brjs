package org.bladerunnerjs.plugin.plugins.bundlers.i18n;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.utility.RelativePathUtility;
import org.bladerunnerjs.utility.UnicodeReader;

public class I18nAssetFile implements Asset
{
	
	class Messages {
		public static final String PROPERTY_NAMESPACE_EXCEPTION = "i18n property '%s' in property file '%s' is invalid. It must start with the same namespace as it's container, '%s'.";
	}
	
	public static final String I18N_REGEX = "([a-z]{2})(_([A-Z]{2}))?";
	public static final String I18N_PROPERTIES_FILE_REGEX = I18N_REGEX+"\\.properties";
	
	private Pattern i18nPropertiesPattern = Pattern.compile(I18N_PROPERTIES_FILE_REGEX);
	
	private AssetLocation assetLocation;
	private File assetFile;
	private String assetPath;
	private String defaultFileCharacterEncoding;

	@Override
	public void initialize(AssetLocation assetLocation, File dir, String assetName) throws AssetFileInstantationException
	{
		try {
			this.assetLocation = assetLocation;
			this.assetFile = new File(dir, assetName);
			assetPath = RelativePathUtility.get(assetLocation.assetContainer().app().dir(), assetFile);
			defaultFileCharacterEncoding = assetLocation.root().bladerunnerConf().getDefaultFileCharacterEncoding();
		}
		catch(ConfigException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Reader getReader() throws IOException
	{
		return new UnicodeReader(assetFile, defaultFileCharacterEncoding);
	}

	@Override
	public AssetLocation assetLocation()
	{
		return assetLocation;
	}
	
	@Override
	public File dir()
	{
		return assetFile.getParentFile();
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
	
	public String getLocaleLanguage()
	{
		return getMatchedValueFromPropertiesPattern(1);
	}
	
	public String getLocaleLocation()
	{
		return getMatchedValueFromPropertiesPattern(3);
	}

	public Map<String,String> getLocaleProperties() throws IOException, RequirePathException, NamespaceException
	{
		Map<String, String> propertiesMap = new HashMap<String,String>();
		
		Properties i18nProperties = new Properties();
		i18nProperties.load( new UnicodeReader(assetFile, defaultFileCharacterEncoding) );
		
		for (String property : i18nProperties.stringPropertyNames())
		{
			assetLocation().assertIdentifierCorrectlyNamespaced(property);
			String value = i18nProperties.getProperty(property);
			propertiesMap.put(property, value.replaceAll("\n", "\\\\n"));
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
