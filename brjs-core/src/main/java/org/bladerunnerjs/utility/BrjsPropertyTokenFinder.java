package org.bladerunnerjs.utility;

import java.util.Properties;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.appserver.util.TokenFinder;
import org.bladerunnerjs.appserver.util.TokenReplacementException;


public class BrjsPropertyTokenFinder implements TokenFinder
{

	private Properties props = new Properties();

	public BrjsPropertyTokenFinder(App app, Locale appLocale, String version)
	{
		props.put("BRJS.APP.LOCALE", appLocale.toString());
		props.put("BRJS.APP.NAME", app.getName());
		props.put("BRJS.APP.VERSION", version);
		props.put("BRJS.BUNDLE.PATH", AppMetadataUtility.getRelativeVersionedBundlePath(app, version, "").replaceFirst("/$", ""));
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
