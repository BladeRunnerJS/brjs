package org.bladerunnerjs.plugin.plugins.brjsconformant;

import javax.validation.constraints.NotNull;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.utility.ConfigValidationChecker;
import org.bladerunnerjs.utility.RelativePathUtility;
import org.bladerunnerjs.yaml.AbstractYamlConfFile;


public class BRLibYamlConf extends AbstractYamlConfFile {	
	
	public class Messages {
		public static final String INVALID_REQUIRE_PREFIX_EXCEPTION = "Require prefix '%s' in BR conf at '%s' isn't valid. It must be in the format '%s'";
	}
	
	public static final String REQUIRE_PREFIX_REGEX = "[a-zA-Z0-9]+((/[a-zA-Z0-9]+)+)?";
	
	@NotNull
	public String requirePrefix;
	
	@Override
	public void initialize() {
		requirePrefix = getDefault(requirePrefix, "");
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
			String manifestPath = RelativePathUtility.get(node.root(), this.node.root().dir(), getUnderlyingFile());
			throw new ConfigException( String.format(Messages.INVALID_REQUIRE_PREFIX_EXCEPTION, requirePrefix, manifestPath, REQUIRE_PREFIX_REGEX) );
		}
	}
}
