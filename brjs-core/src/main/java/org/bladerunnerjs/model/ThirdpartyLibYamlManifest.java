package org.bladerunnerjs.model;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.utility.ConfigValidationChecker;
import org.bladerunnerjs.yaml.AbstractYamlConfFile;


public class ThirdpartyLibYamlManifest extends AbstractYamlConfFile
{
	
	public String depends;
	public String js;
	public String css;
	public String exports;
	
	@Override
	public void initialize() {
		depends = getDefault(depends, "");
		js = getDefault(js, "");
		css = getDefault(css, "");
		exports = getDefault(exports, "{}");
	}
	
	@Override
	public void verify() throws ConfigException {
		ConfigValidationChecker.validate(this);
	}
}
