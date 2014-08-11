package org.bladerunnerjs.model;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.yaml.YamlBladerunnerConf;

public class BladerunnerConf extends ConfFile<YamlBladerunnerConf> {
	
	public static final String OUTPUT_ENCODING = "UTF-8";
	
	public BladerunnerConf(BRJS brjs) throws ConfigException {
		super(brjs, YamlBladerunnerConf.class, getConfigFilePath(brjs), "UTF-8");
	}
	
	public int getJettyPort() throws ConfigException {
		reloadConfIfChanged();
		return conf.jettyPort;
	}
	
	public void setJettyPort(int jettyPort) throws ConfigException {
		conf.jettyPort = jettyPort;;
		verifyAndAutoWrite();
	}
	
	public String getDefaultFileCharacterEncoding() throws ConfigException {
		reloadConfIfChanged();
		return conf.defaultFileCharacterEncoding;
	}
	
	public void setDefaultFileCharacterEncoding(String defaultFileCharacterEncoding) throws ConfigException {
		conf.defaultFileCharacterEncoding = defaultFileCharacterEncoding;
		verifyAndAutoWrite();
	}

	public String getLoginRealm() throws ConfigException
	{
		reloadConfIfChanged();
		return conf.loginRealm;
	}

	public String getLoginModuleName() throws ConfigException
	{
		reloadConfIfChanged();
		return conf.LOGIN_MODULE_NAME;
	}
	
	public String[] getIgnoredPaths() throws ConfigException {
		reloadConfIfChanged();
		String[] ignoredFileStrings = conf.ignoredPaths.split("\\s*,\\s*");
		return ignoredFileStrings;
	}
	
	public void setIgnoredPaths(String... ignoredFiles) throws ConfigException {
		conf.ignoredPaths = StringUtils.join(ignoredFiles,",");
		verifyAndAutoWrite();
	}
	
	public static File getConfigFilePath(BRJS brjs) {
		return brjs.conf().file("brjs.conf");
	}
	
}
