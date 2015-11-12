package org.bladerunnerjs.plugin.bundlers.i18n;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.memoization.Getter;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.NamespaceException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.utility.RequirePathUtility;
import org.bladerunnerjs.model.AssetContainer;
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
	private String requirePath;
	private MemoizedValue< Map<String,String> > memoizedLocaleProperties;
	
	public I18nFileAsset(MemoizedFile i18nFile, AssetContainer assetContainer, String requirePrefix)
	{
		this.assetContainer = assetContainer;
		this.assetFile = i18nFile;
		assetPath = assetContainer.app().dir().getRelativePath(assetFile);
		requirePath = calculateRequirePath(requirePrefix, i18nFile);
		try
		{
			defaultFileCharacterEncoding = assetContainer.root().bladerunnerConf().getDefaultFileCharacterEncoding();
		}
		catch (ConfigException ex)
		{
			throw new RuntimeException(ex);
		}
		locale = Locale.createLocaleFromFilepath(getAssetName());
		memoizedLocaleProperties = new MemoizedValue<>(getAssetPath()+" - getLocaleProperties()", assetContainer.root(), file().getUnderlyingFile());
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
		return Arrays.asList(requirePath);
	}
	
	@Override
	public String getPrimaryRequirePath() {
		return requirePath;
	}

	public Map<String,String> getLocaleProperties() throws IOException, RequirePathException, NamespaceException
	{
		try {
    		return memoizedLocaleProperties.value(new Getter<Exception>() {
    			@Override
    			public Object get() throws Exception {
        			Map<String, String> propertiesMap = new LinkedHashMap<String,String>();
        			Properties i18nProperties = new Properties();
        			
        			try(Reader propertiesReader = new UnicodeReader(assetFile, defaultFileCharacterEncoding)) {
        				i18nProperties.load( propertiesReader );
        				
        				for (String property : i18nProperties.stringPropertyNames())
        				{
        					RequirePathUtility.assertIdentifierCorrectlyNamespaced(assetContainer, property);
        					String value = i18nProperties.getProperty(property);
        					propertiesMap.put(property.toLowerCase(), value.replaceAll("\n", "\\\\n"));
        				}
        			}
        			
        			return propertiesMap;
    			}
    		});
		} catch (Exception ex) { // we need to do this because the memoized values don't allow throwing multiple types of exception, so we throw and catch the generic 'Exception' type and cast it
			if (ex instanceof IOException) {
				throw (IOException) ex;
			}
			if (ex instanceof RequirePathException) {
				throw (RequirePathException) ex;
			}
			if (ex instanceof NamespaceException) {
				throw (NamespaceException) ex;
			}
			throw new RuntimeException(ex);
		}
		
	}
	
	public Locale getLocale() {
		return locale;
	}

	@Override
	public AssetContainer assetContainer()
	{
		return assetContainer;
	}
	
	@Override
	public boolean isRequirable()
	{
		return true;
	}

	public static String calculateRequirePath(String requirePrefix, MemoizedFile assetFile)
	{
		return requirePrefix+"/"+assetFile.requirePathName();
	}
	
	@Override
	public boolean isScopeEnforced()
	{
		return true;
	}
	
}
