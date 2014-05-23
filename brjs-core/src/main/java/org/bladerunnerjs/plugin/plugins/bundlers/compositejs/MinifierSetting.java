package org.bladerunnerjs.plugin.plugins.bundlers.compositejs;

import java.util.Map;

public class MinifierSetting 
{
	private String devSetting;
	private String prodSetting;
	
	private static final String DEV_MINIFIER_ATTRIBUTE = "dev-minifier";
	private static final String PROD_MINIFIER_ATTRIBUTE = "prod-minifier";
	
	// Minifier Types
	public static final String SEPARATE_JS_FILES = "none";
	public static final String CONCATINATED_JS_FILES = "combined";

	
	public MinifierSetting(Map<String, String> tagAttributes) 
	{
		devSetting = (tagAttributes.containsKey(DEV_MINIFIER_ATTRIBUTE)) ? tagAttributes.get(DEV_MINIFIER_ATTRIBUTE) : SEPARATE_JS_FILES;
		prodSetting = (tagAttributes.containsKey(PROD_MINIFIER_ATTRIBUTE)) ? tagAttributes.get(PROD_MINIFIER_ATTRIBUTE) : CONCATINATED_JS_FILES;
	}
	
	public String devSetting() {
		return devSetting;
	}

	public String prodSetting() {
		return prodSetting;
	}
}
