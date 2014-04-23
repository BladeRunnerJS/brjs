package org.bladerunnerjs.model;

import javax.validation.constraints.NotNull;

import org.apache.bval.constraints.NotEmpty;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.utility.ConfigValidationChecker;
import org.bladerunnerjs.yaml.AbstractYamlConfFile;


public class YamlNonBladerunnerLibManifest extends AbstractYamlConfFile
{
	
	public String depends;
	public String js;
	public String css;
	
	@NotNull
	@NotEmpty
	public String exports;
	
	@Override
	public void initialize() {
		depends = getDefault(depends, "");
		js = getDefault(js, "");
		css = getDefault(css, "");
		exports = getDefault(exports, "");
	}
	
	@Override
	public void verify() throws ConfigException {
		ConfigValidationChecker.validate(this);
	}
}
