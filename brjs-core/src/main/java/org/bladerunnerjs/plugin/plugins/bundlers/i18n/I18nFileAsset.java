package org.bladerunnerjs.plugin.plugins.bundlers.i18n;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.utility.PrimaryRequirePathUtility;
import org.bladerunnerjs.utility.RelativePathUtility;
import org.bladerunnerjs.utility.UnicodeReader;

public class I18nFileAsset implements Asset
{
	
	class Messages {
		public static final String PROPERTY_NAMESPACE_EXCEPTION = "i18n property '%s' in property file '%s' is invalid. It must start with the same namespace as it's container, '%s'.";
	}
	
	private AssetLocation assetLocation;
	private File assetFile;
	private String assetPath;
	private String defaultFileCharacterEncoding;
	private Locale locale;
	
	public I18nFileAsset(File assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		try {
			this.assetLocation = assetLocation;
			this.assetFile = assetFile;
			assetPath = RelativePathUtility.get(assetLocation.root(), assetLocation.assetContainer().app().dir(), assetFile);
			defaultFileCharacterEncoding = assetLocation.root().bladerunnerConf().getDefaultFileCharacterEncoding();
			locale = Locale.createLocaleFromFilepath(getAssetName());
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
	
	@Override
	public List<String> getRequirePaths() {
		// TODO: we should return the complete list of i18n tokens
		return Collections.emptyList();
	}
	
	@Override
	public String getPrimaryRequirePath() {
		return PrimaryRequirePathUtility.getPrimaryRequirePath(this);
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
	
	public Locale getLocale() {
		return locale;
	}

}
