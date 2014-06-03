package org.bladerunnerjs.plugin.plugins.brjsconformant;

import org.bladerunnerjs.model.ConfFile;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.exception.ConfigException;

public class BRLibConf extends ConfFile<BRLibYamlConf> {
	
	private static final String BR_MANIFEST_FILENAME = "br-lib.conf";

	private JsLib lib;
	
	public BRLibConf(JsLib lib) throws ConfigException {
		super(lib, BRLibYamlConf.class, lib.file(BR_MANIFEST_FILENAME));
		this.lib = lib;
	}
	
	public String getRequirePrefix() throws ConfigException {
		reloadConfIfChanged();
		return conf.requirePrefix;
	}
	
	public void setRequirePrefix(String requirePrefix) throws ConfigException {
		conf.requirePrefix = requirePrefix;
		verifyAndAutoWrite();
	}
	
	public boolean manifestExists()
	{
		return lib.file(BR_MANIFEST_FILENAME).isFile();
	}
	
}
