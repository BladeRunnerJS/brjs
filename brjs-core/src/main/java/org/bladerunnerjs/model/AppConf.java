package org.bladerunnerjs.model;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.yaml.YamlAppConf;

public class AppConf extends ConfFile<YamlAppConf> {
	
	public AppConf(BRJS brjs, File confFile) throws ConfigException {
		super(brjs, YamlAppConf.class, confFile);
	}
	
	public AppConf(App app) throws ConfigException {
		super(app, YamlAppConf.class, app.file("app.conf"));
	}
	
	public String getRequirePrefix() throws ConfigException {
		return getConf().requirePrefix;
	}
	
	public void setRequirePrefix(String requirePrefix) throws ConfigException {
		getConf().requirePrefix = requirePrefix;
		verifyAndAutoWrite();
	}
	
	public Locale[] getLocales() throws ConfigException {
		String[] localeStrings = getConf().locales.split("\\s*,\\s*");
		Locale[] locales = new Locale[localeStrings.length];
		for (int i = 0; i < localeStrings.length; i++) {
			locales[i] = new Locale(localeStrings[i]);
		}
		return locales;
	}
	
	public void setLocales(Locale[] locales) throws ConfigException {
		getConf().locales = StringUtils.join(locales,",");
		verifyAndAutoWrite();
	}
	
	public Locale getDefaultLocale() throws ConfigException {
		return getLocales()[0];
	}
	
	public String getLocaleCookieName() throws ConfigException {
		return getConf().localeCookieName;
	}
	
	public void setLocaleCookieName(String cookieName) throws ConfigException {
		getConf().localeCookieName = cookieName;
		verifyAndAutoWrite();
	}
}
