package org.bladerunnerjs.model;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.yaml.YamlBladerunnerConf;

public class BladerunnerConf extends ConfFile<YamlBladerunnerConf> {
	public BladerunnerConf(BRJS brjs) throws ConfigException {
		super(brjs, YamlBladerunnerConf.class, brjs.file("conf/bladerunner.conf"));
	}
	
	public int getJettyPort() throws ConfigException {
		reloadConf();
		return conf.jettyPort;
	}
	
	public void setJettyPort(int jettyPort) throws ConfigException {
		conf.jettyPort = jettyPort;;
		verifyAndAutoWrite();
	}
	
	public String getDefaultInputEncoding() throws ConfigException {
		reloadConf();
		return conf.defaultInputEncoding;
	}
	
	public void setDefaultInputEncoding(String defaultInputEncoding) throws ConfigException {
		conf.defaultInputEncoding = defaultInputEncoding;
		verifyAndAutoWrite();
	}
	
	public String getDefaultOutputEncoding() throws ConfigException {
		reloadConf();
		return conf.defaultOutputEncoding;
	}
	
	public void setDefaultOutputEncoding(String defaultOutputEncoding) throws ConfigException {
		conf.defaultOutputEncoding = defaultOutputEncoding;
		verifyAndAutoWrite();
	}

	public String getLoginRealm() throws ConfigException
	{
		reloadConf();
		return conf.loginRealm;
	}

	public String getLoginModuleName() throws ConfigException
	{
		reloadConf();
		return conf.LOGIN_MODULE_NAME;
	}
}
