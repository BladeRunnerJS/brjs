package org.bladerunnerjs.model;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.utility.ConfigValidationChecker;
import org.bladerunnerjs.yaml.AbstractYamlConfFile;


public class YamlNonBladerunnerLibManifest extends AbstractYamlConfFile
{
	
	public String depends;
	
	public String js;
	
	public String css;
	
	@Override
	public void initialize() {
		depends = "";
		js = "";
		css = "";
	}
	
	@Override
	public void verify() throws ConfigException {
		ConfigValidationChecker.validate(this);
	}
}
