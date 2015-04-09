package org.bladerunnerjs.model;

import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.utility.ConfigValidationChecker;
import org.bladerunnerjs.yaml.AbstractYamlConfFile;


public class ThirdpartyLibYamlManifest extends AbstractYamlConfFile
{
	
	public String depends;
	public String js;
	public String css;
	public String exports;
	public boolean commonjsDefinition;
	
	@Override
	public void initialize(BRJSNode node) {
		depends = getDefault(depends, "");
		js = getDefault(js, "");
		css = getDefault(css, "");
		String defaultExports = "window."+node.dir().getName().replaceAll("\\.-_+%","");
		exports = getDefault(exports, defaultExports);
		commonjsDefinition = getDefault(commonjsDefinition, false);
	}
	
	@Override
	public void verify() throws ConfigException {
		ConfigValidationChecker.validate(this);
	}
}
