package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.utility.FileModifiedChecker;
import org.bladerunnerjs.yaml.AbstractYamlConfFile;
import org.bladerunnerjs.yaml.ConfFactory;


public class ConfFile<CF extends AbstractYamlConfFile> {
	protected CF conf;
	
	private final Node node;
	private final Class<CF> confClass;
	private final File confFile;
	private FileModifiedChecker fileModifiedChecker;
	private boolean shouldAutoWriteOnSet = true;
	private boolean hasUnwrittenChanges = false;
	
	public ConfFile(Node node, Class<CF> confClass, File confFile) throws ConfigException {
		this.node = node;
		this.confClass = confClass;
		this.confFile = confFile;
		this.conf = ConfFactory.createConfFile(node, confClass, confFile);
		fileModifiedChecker = new FileModifiedChecker(confFile);
		fileModifiedChecker.fileModifiedSinceLastCheck();
	}
	
	public void write() throws ConfigException {
		try {
			conf.write();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void reloadConfIfChanged() throws ConfigException {
		if (fileModifiedChecker.fileModifiedSinceLastCheck() && !hasUnwrittenChanges) {
			conf = ConfFactory.createConfFile(node, confClass, confFile);
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
	
	public void autoWriteOnSet(boolean shouldAutoWriteOnSet)
	{
		this.shouldAutoWriteOnSet = shouldAutoWriteOnSet;
	}
	
	protected void verifyAndAutoWrite() throws ConfigException
	{
		conf.verify();
		if (shouldAutoWriteOnSet)
		{
			write();
			hasUnwrittenChanges = false;
		}
		else
		{
			hasUnwrittenChanges = true;
		}
	}
}
