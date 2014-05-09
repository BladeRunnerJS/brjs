package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.yaml.YamlAppConf;

public class AppConf extends ConfFile<YamlAppConf> {
	
	public AppConf(File confFile) throws ConfigException {
		super(null, YamlAppConf.class, confFile);	//TODO: remove this - we only need it while we have to use servlets in Wars
	}
	
	public AppConf(App app) throws ConfigException {
		super(app, YamlAppConf.class, app.file("app.conf"));
	}
	
	public String getRequirePrefix() throws ConfigException {
		reloadConfIfChanged();
		return conf.requirePrefix;
	}
	
	public void setRequirePrefix(String requirePrefix) throws ConfigException {
		conf.requirePrefix = requirePrefix;
		verifyAndAutoWrite();
	}
	
	public String getLocales() throws ConfigException {
		reloadConfIfChanged();
		return conf.locales.replace(" ", "");
	}
	
	public void setLocales(String locales) throws ConfigException {
		conf.locales = locales.replace(" ", "");
		verifyAndAutoWrite();
	}
}
