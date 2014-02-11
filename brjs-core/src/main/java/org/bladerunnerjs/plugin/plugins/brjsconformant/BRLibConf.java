package org.bladerunnerjs.plugin.plugins.brjsconformant;

import javax.validation.constraints.NotNull;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.utility.ConfigValidationChecker;
import org.bladerunnerjs.yaml.AbstractYamlConfFile;


public class BRLibConf extends AbstractYamlConfFile {	
	
	public class Messages {
		public static final String INVALID_REQUIRE_PREFIX_EXCEPTION = "Require prefix in BR manifest at '%s' isn't valid. It must be in the format '%s'";
	}
	
	public static final String REQUIRE_PREFIX_REGEX = "[a-zA-Z]+((/[a-zA-Z]+)+)?";
	
	@NotNull
	public String requirePrefix;
	
	@Override
	public void initialize() {
		requirePrefix = "";
	}
	
	@Override
	public void verify() throws ConfigException {
		ConfigValidationChecker.validate(this);
		verifyRequirePrefix(requirePrefix);
	}
	
	private void verifyRequirePrefix(String requirePrefix) throws ConfigException {
		if (requirePrefix.equals(""))
		{
			return;
		}
		
		if (!requirePrefix.matches(REQUIRE_PREFIX_REGEX))
		{
			throw new ConfigException( String.format(Messages.INVALID_REQUIRE_PREFIX_EXCEPTION, getUnderlyingFile(), REQUIRE_PREFIX_REGEX) );
		}
	}
}
