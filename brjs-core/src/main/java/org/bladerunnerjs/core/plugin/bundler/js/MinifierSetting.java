package org.bladerunnerjs.core.plugin.bundler.js;

import java.util.Map;

public class MinifierSetting 
{
	private String devSetting;
	private String prodSetting;

	public class MinifierTypes 
	{
		public static final String SEPARATE_JS_FILES = "none";
		public static final String CONCATINATED_JS_FILES = "combined";
	}
	
	public MinifierSetting(Map<String, String> tagAttributes) {
		devSetting = (tagAttributes.containsKey("dev")) ? tagAttributes.get("dev") : MinifierTypes.SEPARATE_JS_FILES;
		prodSetting = (tagAttributes.containsKey("prod")) ? tagAttributes.get("prod") : MinifierTypes.CONCATINATED_JS_FILES;
	}
	
	public String devSetting() {
		return devSetting;
	}

	public String prodSetting() {
		return prodSetting;
	}
}
