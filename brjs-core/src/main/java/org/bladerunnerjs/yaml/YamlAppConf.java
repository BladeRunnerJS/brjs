package org.bladerunnerjs.yaml;

import javax.validation.constraints.NotNull;

import org.apache.bval.constraints.NotEmpty;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.name.InvalidPackageNameException;
import org.bladerunnerjs.utility.ConfigValidationChecker;
import org.bladerunnerjs.utility.NameValidator;


public class YamlAppConf extends AbstractYamlConfFile {
	@NotNull
	@NotEmpty
	public String appNamespace;
	
	@NotNull
	@NotEmpty
	public String locales;
	
	@Override
	public void initialize() {
		appNamespace = "appns";
		locales = "en";
	}
	
	@Override
	public void verify() throws ConfigException {
		try {
			ConfigValidationChecker.validate(this);
			NameValidator.assertValidPackageName(node, appNamespace);
			verifyLocales(locales);
		}
		catch(InvalidPackageNameException e) {
			throw new ConfigException(e);
		}
	}
	
	private void verifyLocales(String locales) throws ConfigException {
		for(String locale : locales.split("\\s*,\\s*")) {
			if(!locale.matches("^[a-z]{2}(_[A-Z]{2})?$")) {
				throw new ConfigException("'" + locale + "' not a valid locale within '" + getUnderlyingFile().getPath() + "'");
			}
		}
	}
}
