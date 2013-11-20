package org.bladerunnerjs.core.plugin.bundler.js;

import java.util.Map;

public class MinifierSetting 
{
	private String devSetting;
	private String prodSetting;

	public class MinifierTags
	{
		public static final String DEV_TAG = "dev-minifier";
		public static final String PROD_TAG = "prod-minifier";
	}
	
	public class MinifierTypes 
	{
		public static final String SEPARATE_JS_FILES = "none";
		public static final String CONCATINATED_JS_FILES = "combined";
	}
	
	public MinifierSetting(Map<String, String> tagAttributes) 
	{
		devSetting = (tagAttributes.containsKey(MinifierTags.DEV_TAG)) ? tagAttributes.get(MinifierTags.DEV_TAG) : MinifierTypes.SEPARATE_JS_FILES;
		prodSetting = (tagAttributes.containsKey(MinifierTags.PROD_TAG)) ? tagAttributes.get(MinifierTags.PROD_TAG) : MinifierTypes.CONCATINATED_JS_FILES;
	}
	
	public String devSetting() {
		return devSetting;
	}

	public String prodSetting() {
		return prodSetting;
	}
}
