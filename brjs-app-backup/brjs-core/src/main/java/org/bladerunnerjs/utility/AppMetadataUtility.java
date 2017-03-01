package org.bladerunnerjs.utility;

import org.bladerunnerjs.api.App;


public class AppMetadataUtility
{
	public static final String APP_VERSION_TOKEN = "@appVersion@";
	public static final String XML_BUNDLE_PATH_TOKEN = "@bundlePath@";
	
	public static final String DEPRECATED_TOKEN_WARNING = "The token '%s' is deprecated and should not be used as it may be removed in future releases. '%s' should be used instead.";
	
	public static String getRelativeVersionedBundlePath(App app, String version, String bundlePath) {
		return "v/"+version+bundlePath;
	}
}
