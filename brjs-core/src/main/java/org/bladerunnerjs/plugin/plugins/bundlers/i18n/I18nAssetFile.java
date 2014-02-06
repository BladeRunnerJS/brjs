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

import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.utility.RelativePathUtility;

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

	@Override
	public void initialize(AssetLocation assetLocation, File assetFileOrDir) throws AssetFileInstantationException
	{
		this.assetLocation = assetLocation;
		this.assetFile = assetFileOrDir;
		assetPath = RelativePathUtility.get(assetLocation.getAssetContainer().getApp().dir(), assetFileOrDir);
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
		i18nProperties.load( new FileReader(assetFile) );
		
		for (String property : i18nProperties.stringPropertyNames())
		{
			assertPropertyMatchesNamepsaceOfAssetLocation(property);
			String value = i18nProperties.getProperty(property);
			propertiesMap.put(property, value);
		}

		return propertiesMap;
	}
	
	private void assertPropertyMatchesNamepsaceOfAssetLocation(String property) throws RequirePathException, NamespaceException
	{
		String propertyNamepsace = getAssetLocation().getNamespace();
		if (!property.startsWith(propertyNamepsace+"."))
		{
			throw new NamespaceException( String.format(Messages.PROPERTY_NAMESPACE_EXCEPTION, property, this.getAssetPath(), propertyNamepsace) );
		}
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
