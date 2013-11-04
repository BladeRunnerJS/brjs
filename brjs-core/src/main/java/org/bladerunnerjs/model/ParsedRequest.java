package org.bladerunnerjs.model;

import java.util.HashMap;
import java.util.Map;

public class ParsedRequest {
	public ParsedRequest(String formName) {
		this.formName = formName;
	}
	
	public String formName;
	public Map<String, String> properties = new HashMap<>();
}
