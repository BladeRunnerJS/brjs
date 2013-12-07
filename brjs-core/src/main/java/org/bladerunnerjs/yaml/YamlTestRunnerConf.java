package org.bladerunnerjs.yaml;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.bval.constraints.NotEmpty;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.utility.ConfigValidationChecker;


public class YamlTestRunnerConf extends AbstractYamlConfFile {
	@NotNull
	@NotEmpty
	public String defaultBrowser;
	
	public Map<String, Map<String, String>> browserPaths = new HashMap<>();
	
	@Override
	public void initialize() {
		defaultBrowser = "chrome";
		browserPaths.put("windows", new HashMap<String, String>());
		browserPaths.put("mac", new HashMap<String, String>());
		browserPaths.put("linux", new HashMap<String, String>());
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
