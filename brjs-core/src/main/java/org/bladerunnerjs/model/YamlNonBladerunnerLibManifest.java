package org.bladerunnerjs.model;

import org.bladerunnerjs.model.conf.AbstractYamlConfFile;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.utility.ConfigValidationChecker;


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
