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
	public void initialize(BRJSNode node) {
		depends = getDefault(depends, "");
		js = getDefault(js, "");
		css = getDefault(css, "");
		String defaultExports = node.dir().getName().replaceAll("\\.-_+%","");
		exports = getDefault(exports, defaultExports);
	}
	
	@Override
	public void verify() throws ConfigException {
		ConfigValidationChecker.validate(this);
	}
}
