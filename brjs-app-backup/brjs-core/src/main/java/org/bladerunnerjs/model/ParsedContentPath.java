package org.bladerunnerjs.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class ParsedContentPath {
	public ParsedContentPath(String formName) {
		this.formName = formName;
	}
	
	public String formName;
	public Map<String, String> properties = new LinkedHashMap<>();
}
