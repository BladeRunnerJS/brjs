package org.bladerunnerjs.utility;

import java.util.Properties;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.appserver.util.TokenFinder;
import org.bladerunnerjs.appserver.util.TokenReplacementException;


public class BrjsPropertyTokenFinder implements TokenFinder
{

	public static String APP_LOCALE_KEY = "BRJS.APP.LOCALE";
	public static String APP_NAME_KEY = "BRJS.APP.NAME";
	public static String APP_VERSION_KEY = "BRJS.APP.VERSION";
	public static String BUNDLE_PATH_KEY = "BRJS.BUNDLE.PATH";
	
	private Properties props = new Properties(); 
	
	public BrjsPropertyTokenFinder(App app, Locale appLocale, String version)
	{
		props.put(APP_LOCALE_KEY, appLocale.toString());
		props.put(APP_NAME_KEY, app.getName());
		props.put(APP_VERSION_KEY, version);
		props.put(BUNDLE_PATH_KEY, AppMetadataUtility.getRelativeVersionedBundlePath(app, version, "").replaceFirst("/$", ""));
	}

	@Override
	public String findTokenValue(String tokenName) throws TokenReplacementException
	{
		String value = props.getProperty(tokenName);
		if (value != null) {
			return value;
		}
		throw new TokenReplacementException( String.format("'%s' is not a valid BRJS token", tokenName) , this.getClass());
	}

}
