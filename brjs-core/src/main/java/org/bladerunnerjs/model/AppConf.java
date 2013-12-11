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
	
	public String getAppNamespace() throws ConfigException {
		reloadConf();
		return conf.appNamespace;
	}
	
	public void setAppNamespace(String appNamespace) throws ConfigException {
		conf.appNamespace = appNamespace;
		verifyAndAutoWrite();
	}
	
	public String getLocales() throws ConfigException {
		reloadConf();
		return conf.locales;
	}
	
	public void setLocales(String locales) throws ConfigException {
		conf.locales = locales;
		verifyAndAutoWrite();
	}
}
