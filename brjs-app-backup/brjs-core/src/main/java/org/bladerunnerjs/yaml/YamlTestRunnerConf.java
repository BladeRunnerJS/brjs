package org.bladerunnerjs.yaml;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.bval.constraints.NotEmpty;
import org.bladerunnerjs.api.BRJSNode;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.utility.ConfigValidationChecker;


public class YamlTestRunnerConf extends AbstractYamlConfFile {
	@NotNull
	@NotEmpty
	public String defaultBrowser;
	
	public Map<String, Map<String, String>> browserPaths = new LinkedHashMap<>();
	
	@Override
	public void initialize(BRJSNode node) {
		defaultBrowser = getDefault(defaultBrowser, "chrome");
		browserPaths.put("windows", new LinkedHashMap<String, String>());
		browserPaths.put("mac", new LinkedHashMap<String, String>());
		browserPaths.put("linux", new LinkedHashMap<String, String>());
		browserPaths.put("linux64", new LinkedHashMap<String, String>());
	}
	
	@Override
	public void verify() throws ConfigException {
		ConfigValidationChecker.validate(this);
		verifyDefualtBrowserIsAvailableOnAllOperatingSystems();
	}
	
	private void verifyDefualtBrowserIsAvailableOnAllOperatingSystems() {
		// TODO
	}
}
