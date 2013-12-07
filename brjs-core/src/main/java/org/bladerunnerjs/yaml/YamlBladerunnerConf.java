package org.bladerunnerjs.yaml;

import java.nio.charset.Charset;

import javax.validation.constraints.*;

import org.apache.bval.constraints.NotEmpty;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.utility.ConfigValidationChecker;


public class YamlBladerunnerConf extends AbstractYamlConfFile {
	@Min(value=1)
	@Max(value=65535)
	public int jettyPort;
	
	@NotNull
	@NotEmpty
	public String defaultInputEncoding;
	
	@NotNull
	@NotEmpty
	public String defaultOutputEncoding;

	@NotNull
	@NotEmpty
	public String loginRealm;

	@NotNull
	@NotEmpty
	public final String LOGIN_MODULE_NAME = "BladeRunnerLoginModule";
	
	@Override
	public void initialize() {
		jettyPort = 7070;
		defaultInputEncoding = "UTF-8";
		defaultOutputEncoding = "UTF-8";
		loginRealm = "BladeRunnerLoginRealm";
	}
	
	@Override
	public void verify() throws ConfigException {
		ConfigValidationChecker.validate(this);
		verifyCharacterEncodings();
	}
	
	private void verifyCharacterEncodings() throws ConfigException {
		verifyCharacterEncoding("defaultInputEncoding", defaultInputEncoding);
		verifyCharacterEncoding("defaultOutputEncoding", defaultOutputEncoding);
	}
	
	private void verifyCharacterEncoding(String propertyName, String characterEncoding) throws ConfigException {
		if(!Charset.isSupported(characterEncoding)) {
			throw new ConfigException("the '" + propertyName + "' in '" + getUnderlyingFile().getPath() + "' is specified as '" +
				characterEncoding + "' which is not a valid character encoding");
		}
	}
}
