package org.bladerunnerjs.plugin.brjsconformant;

import org.bladerunnerjs.api.ConfFile;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.model.exception.ConfigException;

public class BRLibConf extends ConfFile<BRLibYamlConf> {
	
	public static final String BR_CONF_FILENAME = "br-lib.conf";

	private JsLib lib;
	
	public BRLibConf(JsLib lib) throws ConfigException {
		super(lib, BRLibYamlConf.class, lib.file(BR_CONF_FILENAME));
		this.lib = lib;
	}
	
	public String getRequirePrefix() throws ConfigException {
		return getConf().requirePrefix;
	}
	
	public void setRequirePrefix(String requirePrefix) throws ConfigException {
		getConf().requirePrefix = requirePrefix;
		verifyAndAutoWrite();
	}
	
	public boolean manifestExists()
	{
		return lib.file(BR_CONF_FILENAME).isFile();
	}
	
}
