package org.bladerunnerjs.yaml;

import javax.validation.constraints.NotNull;

import org.apache.bval.constraints.NotEmpty;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.name.InvalidPackageNameException;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.utility.ConfigValidationChecker;
import org.bladerunnerjs.utility.NameValidator;


public class YamlAppConf extends AbstractYamlConfFile {
	public class Messages {
		public static final String APP_NAMESPACE_PROPERTY_DEPRECATED = "The 'appNamespace' property within 'app.conf' is deprecated, and it should be renamed to 'requirePrefix' instead.";
	}
	
	public String appNamespace;
	
	@NotNull
	@NotEmpty
	public String requirePrefix;
	
	@NotNull
	@NotEmpty
	public String locales;
	
	public String localeCookieName;
	
	@Override
	public void initialize(BRJSNode node) {
		if(appNamespace != null) {
			Logger logger = node.root().getLoggerFactory().getLogger(YamlAppConf.class);
			logger.warn(Messages.APP_NAMESPACE_PROPERTY_DEPRECATED);
		}
		
		requirePrefix = getDefault(requirePrefix, (appNamespace != null) ? appNamespace : "appns");
		locales = getDefault(locales, "en");
		localeCookieName = getDefault(localeCookieName, "BRJS.LOCALE");
	}
	
	@Override
	public void verify() throws ConfigException {
		try {
			if((appNamespace != null) && (appNamespace != requirePrefix)) throw new ConfigException("The 'appNamespace' and 'requirePrefix' properties within 'app.conf' should not both be defined.");
			
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
