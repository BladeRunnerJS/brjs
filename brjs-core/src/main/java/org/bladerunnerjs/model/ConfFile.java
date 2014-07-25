package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.utility.filemodification.InfoFileModifiedChecker;
import org.bladerunnerjs.yaml.AbstractYamlConfFile;
import org.bladerunnerjs.yaml.ConfFactory;


public class ConfFile<CF extends AbstractYamlConfFile> {
	protected CF conf;
	
	private final BRJSNode node;
	private final Class<CF> confClass;
	private final File confFile;
	private InfoFileModifiedChecker fileModifiedChecker;
	private boolean shouldAutoWriteOnSet = true;
	private boolean hasUnwrittenChanges = false;

	private String defaultFileCharacterEncoding;
	
	public ConfFile(BRJSNode node, Class<CF> confClass, File confFile) throws ConfigException {
		this(node, confClass, confFile, node.root().bladerunnerConf().getDefaultFileCharacterEncoding());
	}
	
	public ConfFile(BRJSNode node, Class<CF> confClass, File confFile, String defaultFileCharacterEncoding) throws ConfigException {
		this.node = node;
		this.confClass = confClass;
		this.confFile = confFile;
		this.defaultFileCharacterEncoding = defaultFileCharacterEncoding;
		fileModifiedChecker = new InfoFileModifiedChecker(node.root().getFileInfo(confFile));
		this.conf = ConfFactory.createConfFile(node, confClass, confFile, defaultFileCharacterEncoding);
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
		if (fileModifiedChecker.hasChangedSinceLastCheck() && !hasUnwrittenChanges) {
			conf = ConfFactory.createConfFile(node, confClass, confFile, defaultFileCharacterEncoding);
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
