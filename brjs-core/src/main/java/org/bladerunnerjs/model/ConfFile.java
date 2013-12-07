package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.yaml.AbstractYamlConfFile;
import org.bladerunnerjs.yaml.ConfFactory;


public class ConfFile<CF extends AbstractYamlConfFile> {
	protected CF conf;
	
	private final Node node;
	private final Class<CF> confClass;
	private final File confFile;
	private boolean confRefreshRequired = false; // TODO: detect file updates in some way
	
	public ConfFile(Node node, Class<CF> confClass, File confFile) throws ConfigException {
		this.node = node;
		this.confClass = confClass;
		this.confFile = confFile;
		this.conf = ConfFactory.createConfFile(node, confClass, confFile);
	}
	
	public void write() throws ConfigException {
		try {
			conf.write();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void reloadConf() throws ConfigException {
		if(confRefreshRequired) {
			conf = ConfFactory.createConfFile(node, confClass, confFile);
			confRefreshRequired = false;
		}
	}
	
	public File getConfFile()
	{
		return confFile;
	}
	
	public boolean fileExists()
	{
		return getConfFile().isFile();
	}
}
