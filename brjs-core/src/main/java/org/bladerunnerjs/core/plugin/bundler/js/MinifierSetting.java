package org.bladerunnerjs.core.plugin.bundler.js;

import java.util.Map;

public class MinifierSetting {
	private String devSetting;
	private String prodSetting;
	
	public MinifierSetting(Map<String, String> tagAttributes) {
		devSetting = (tagAttributes.containsKey("dev")) ? tagAttributes.get("dev") : "separate";
		prodSetting = (tagAttributes.containsKey("prod")) ? tagAttributes.get("prod") : "combined";
	}
	
	public String devSetting() {
		return devSetting;
	}

	public String prodSetting() {
		return prodSetting;
	}
}
