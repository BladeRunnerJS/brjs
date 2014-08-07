package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.yaml.AbstractYamlConfFile;
import org.bladerunnerjs.yaml.ConfFactory;


public class ConfFile<CF extends AbstractYamlConfFile> {
	private final MemoizedValue<CF> conf;
	private final BRJSNode node;
	private final Class<CF> confClass;
	private final File confFile;
	private boolean shouldAutoWriteOnSet = true;
	
	private String defaultFileCharacterEncoding;
	
	public ConfFile(BRJSNode node, Class<CF> confClass, File confFile) throws ConfigException {
		this(node, confClass, confFile, node.root().bladerunnerConf().getDefaultFileCharacterEncoding());
	}
	
	public ConfFile(BRJSNode node, Class<CF> confClass, File confFile, String defaultFileCharacterEncoding) throws ConfigException {
		this.node = node;
		this.confClass = confClass;
		this.confFile = confFile;
		this.defaultFileCharacterEncoding = defaultFileCharacterEncoding;
		conf = new MemoizedValue<>("ConfFile.conf", node.root(), confFile, node.root().file("conf/brjs.conf"));
		
		// needed to keep the pre-existing tests working
		getConf();
	}
	
	public void write() throws ConfigException {
		try {
			getConf().write();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected CF getConf() throws ConfigException {
		return conf.value(() -> {
			return ConfFactory.createConfFile(node, confClass, confFile, defaultFileCharacterEncoding);
		});
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
		getConf().verify();
		if (shouldAutoWriteOnSet)
		{
			write();
		}
	}
}
