package org.bladerunnerjs.yaml;

import java.nio.charset.Charset;

import javax.validation.constraints.*;

import org.apache.bval.constraints.NotEmpty;
import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.utility.ConfigValidationChecker;


public class YamlBladerunnerConf extends AbstractYamlConfFile {
	
	@Min(value=1)
	@Max(value=65535)
	public int jettyPort;
	
	@NotNull
	@NotEmpty
	public String defaultFileCharacterEncoding;

	@NotNull
	@NotEmpty
	public String loginRealm;

	@NotNull
	@NotEmpty
	public final String LOGIN_MODULE_NAME = "BladeRunnerLoginModule";
	
	@NotNull
	public String ignoredPaths;
	
	@Override
	public void initialize(BRJSNode node) {
		jettyPort = getDefault(jettyPort, 7070);
		defaultFileCharacterEncoding = getDefault(defaultFileCharacterEncoding, "UTF-8");
		loginRealm = getDefault(loginRealm, "BladeRunnerLoginRealm");
		ignoredPaths = getDefault(ignoredPaths, ".svn, .git");
	}
	
	@Override
	public void verify() throws ConfigException {
		ConfigValidationChecker.validate(this);
		verifyCharacterEncodings();
	}
	
	private void verifyCharacterEncodings() throws ConfigException {
		verifyCharacterEncoding("defaultFileCharacterEncoding", defaultFileCharacterEncoding);
	}
	
	private void verifyCharacterEncoding(String propertyName, String characterEncoding) throws ConfigException {
		if(!Charset.isSupported(characterEncoding)) {
			throw new ConfigException("the '" + propertyName + "' in '" + getUnderlyingFile().getPath() + "' is specified as '" +
				characterEncoding + "' which is not a valid character encoding");
		}
	}
}
