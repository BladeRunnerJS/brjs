package org.bladerunnerjs.utility;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.exception.ConfigException;

import com.google.common.base.Joiner;


public class ServedAppMetadataUtility
{
	
	public static final String XML_BUNDLE_PATH_TOKEN = "@bundlePath@";
	public static final String XML_UNVERSIONED_BUNDLE_PATH_TOKEN = "@unversionedBundlePath@";

	public static String getBundlePathJsData(App app, String version) throws ConfigException {
		return "window.$BRJS_APP_VERSION = '"+version+"';\n" +
		"window.$BRJS_BUNDLE_PATH = '../v/"+version+"';\n" +
		"window.$BRJS_UNVERSIONED_BUNDLE_PATH = '..';\n" +
		"window.$BRJS_LOCALE_COOKIE_NAME = '"+app.appConf().getLocaleCookieName()+"';\n" +
		"window.$BRJS_APP_LOCALES = {'" + Joiner.on("':true, '").join(app.appConf().getLocales()) + "':true};\n";
	}
	
	public static String getVersionedBundlePath(String version) {
		return "../v/"+version+"/";
	}
	
	public static String getUnversionedBundlePath() {
		return "../";
	}
	
}
