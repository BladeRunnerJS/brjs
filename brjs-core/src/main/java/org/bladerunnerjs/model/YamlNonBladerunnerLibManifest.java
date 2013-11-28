package org.bladerunnerjs.model;

import javax.validation.constraints.NotNull;

import org.apache.bval.constraints.NotEmpty;
import org.bladerunnerjs.model.conf.AbstractYamlConfFile;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.utility.ConfigValidationChecker;


public class YamlNonBladerunnerLibManifest extends AbstractYamlConfFile
{
	
	@NotNull
	@NotEmpty
	public String depends;
	
	@NotNull
	@NotEmpty
	public String js;
	
	@NotNull
	@NotEmpty
	public String css;
	
	@Override
	public void initialize() {
		depends = "*";
		js = "";
		css = "";
	}
	
	@Override
	public void verify() throws ConfigException {
		ConfigValidationChecker.validate(this);
	}
}
