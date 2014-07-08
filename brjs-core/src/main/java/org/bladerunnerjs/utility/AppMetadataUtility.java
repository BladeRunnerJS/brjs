package org.bladerunnerjs.utility;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.exception.ConfigException;

import com.google.common.base.Joiner;


public class AppMetadataUtility
{
	public static final String APP_VERSION_TOKEN = "@appVersion@";
	public static final String XML_BUNDLE_PATH_TOKEN = "@bundlePath@";
	public static final String XML_UNVERSIONED_BUNDLE_PATH_TOKEN = "@unversionedBundlePath@";

	public static String getBundlePathJsData(App app, String version) throws ConfigException {
		return "window.$BRJS_APP_VERSION = '"+version+"';\n" +
		"window.$BRJS_BUNDLE_PATH = '"+getVersionedBundlePath(version, "")+"';\n" +
		"window.$BRJS_UNVERSIONED_BUNDLE_PATH = '"+getUnversionedBundlePath("")+"';\n" +
		"window.$BRJS_LOCALE_COOKIE_NAME = '"+app.appConf().getLocaleCookieName()+"';\n" +
		"window.$BRJS_APP_LOCALES = {'" + Joiner.on("':true, '").join(app.appConf().getLocales()) + "':true};\n";
	}
	
	public static String getVersionedBundlePath(String version) {
		return getVersionedBundlePath(version, "");
	}
	
	public static String getVersionedBundlePath(String version, String bundlePath) {
		return "../v/"+version+bundlePath;
	}
	
	public static String getUnversionedBundlePath() {
		return getUnversionedBundlePath("");
	}
	
	public static String getUnversionedBundlePath(String bundlePath) {
		return ".."+bundlePath;
	}
	
}
