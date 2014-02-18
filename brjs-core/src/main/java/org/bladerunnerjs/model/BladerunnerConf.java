package org.bladerunnerjs.model;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.yaml.YamlBladerunnerConf;

public class BladerunnerConf extends ConfFile<YamlBladerunnerConf> {
	public BladerunnerConf(BRJS brjs) throws ConfigException {
		super(brjs, YamlBladerunnerConf.class, brjs.file("conf/bladerunner.conf"));
	}
	
	public int getJettyPort() throws ConfigException {
		reloadConfIfChanged();
		return conf.jettyPort;
	}
	
	public void setJettyPort(int jettyPort) throws ConfigException {
		conf.jettyPort = jettyPort;;
		verifyAndAutoWrite();
	}
	
	public String getDefaultInputEncoding() throws ConfigException {
		reloadConfIfChanged();
		return conf.defaultInputEncoding;
	}
	
	public void setDefaultInputEncoding(String defaultInputEncoding) throws ConfigException {
		conf.defaultInputEncoding = defaultInputEncoding;
		verifyAndAutoWrite();
	}
	
	public String getDefaultOutputEncoding() throws ConfigException {
		reloadConfIfChanged();
		return conf.defaultOutputEncoding;
	}
	
	// TODO: change this method, and any related artifacts to omit the word 'default' since it allows you to absolutely define character encoding sent to the browser
	// maybe the terms should be 'default file character encoding' and 'browser character encoding'
	public void setDefaultOutputEncoding(String defaultOutputEncoding) throws ConfigException {
		conf.defaultOutputEncoding = defaultOutputEncoding;
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
}
