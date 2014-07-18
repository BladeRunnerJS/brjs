package org.bladerunnerjs.utility;


public class AppMetadataUtility
{
	public static final String APP_VERSION_TOKEN = "@appVersion@";
	public static final String XML_BUNDLE_PATH_TOKEN = "@bundlePath@";
	
	public static String getPathRelativeToIndexPage(String bundlePath) {
		if (bundlePath.startsWith("/")) {
			return ".."+bundlePath;			
		}
		return "../"+bundlePath;
	}
	
	public static String getRelativeVersionedBundlePath(String version, String bundlePath) {
		return "v/"+version+bundlePath;
	}
	
}
