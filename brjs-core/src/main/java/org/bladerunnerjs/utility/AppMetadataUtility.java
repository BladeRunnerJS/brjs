package org.bladerunnerjs.utility;

import org.bladerunnerjs.model.App;


public class AppMetadataUtility
{
	public static final String APP_VERSION_TOKEN = "@appVersion@";
	public static final String XML_BUNDLE_PATH_TOKEN = "@bundlePath@";
	
	public static String getRelativeVersionedBundlePath(App app, String version, String bundlePath) {
		return ((app.isMultiLocaleApp()) ? "../" : "") + "v/"+version+bundlePath;
	}
}
