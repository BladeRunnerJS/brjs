package org.bladerunnerjs.model;

import org.bladerunnerjs.model.exception.ConfigException;

public class JsLibConf extends ConfFile<YamlJsLibConf> {
	public JsLibConf(JsLib lib) throws ConfigException {
		super(lib, YamlJsLibConf.class, lib.file("lib.conf"));
	}
	
	public String getLibNamespace() throws ConfigException {
		reloadConf();
		return conf.libNamespace;
	}
	
	public void setLibNamespace(String libNamespace) throws ConfigException {
		conf.libNamespace = libNamespace;
		conf.verify();
	}
}
