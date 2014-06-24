package org.bladerunnerjs.yaml;

import javax.validation.constraints.NotNull;

import org.apache.bval.constraints.NotEmpty;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.name.InvalidPackageNameException;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.utility.ConfigValidationChecker;
import org.bladerunnerjs.utility.NameValidator;


public class YamlAppConf extends AbstractYamlConfFile {
	@NotNull
	@NotEmpty
	public String requirePrefix;
	
	@NotNull
	@NotEmpty
	public String locales;
	
	@NotNull
	@NotEmpty
	public String localeCookieName;
	
	@Override
	public void initialize() {
		requirePrefix = getDefault(requirePrefix, "appns");
		locales = getDefault(locales, "en");
		localeCookieName = getDefault(localeCookieName, "BRJS.LOCALE");
	}
	
	@Override
	public void verify() throws ConfigException {
		try {
			ConfigValidationChecker.validate(this);
			NameValidator.assertValidPackageName(node, requirePrefix);
			verifyLocales(locales);
		}
		catch(InvalidPackageNameException e) {
			throw new ConfigException(e);
		}
	}
	
	private void verifyLocales(String locales) throws ConfigException {
		try {
    		String[] localeStrings = locales.split("\\s*,\\s*");
    		for (int i = 0; i < localeStrings.length; i++) {
    			Locale locale = new Locale(localeStrings[i]);
    			if (locale.isEmptyLocale()) {
    				throw new ConfigException("Locales cannot be empty and must be in the format " + Locale.LANGUAGE_AND_COUNTRY_CODE_FORMAT);
    			}
    		}
		} catch (IllegalArgumentException ex) {
			throw new ConfigException("Error in the config file " + getUnderlyingFile().getPath(), ex);
		}
	}
}
