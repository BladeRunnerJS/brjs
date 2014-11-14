package org.bladerunnerjs.model;

import java.io.IOException;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.yaml.AbstractYamlConfFile;
import org.bladerunnerjs.yaml.ConfFactory;


public class ConfFile<CF extends AbstractYamlConfFile> {
	private final MemoizedValue<CF> conf;
	private final BRJSNode node;
	private final Class<CF> confClass;
	private final MemoizedFile confFile;
	private boolean autoWrite = true;
	
	private String defaultFileCharacterEncoding;
	
	public ConfFile(BRJSNode node, Class<CF> confClass, MemoizedFile confFile) throws ConfigException {
		this(node, confClass, confFile, node.root().bladerunnerConf().getDefaultFileCharacterEncoding());
	}
	
	public ConfFile(BRJSNode node, Class<CF> confClass, MemoizedFile confFile, String defaultFileCharacterEncoding) throws ConfigException {
		this.node = node;
		this.confClass = confClass;
		this.confFile = confFile;
		this.defaultFileCharacterEncoding = defaultFileCharacterEncoding;
		conf = new MemoizedValue<>("ConfFile.conf", node.root(), confFile);
		
		// needed to keep the pre-existing tests working
		getConf();
	}
	
	public void write() throws ConfigException {
		try {
			getConf().write();
			node.root().getFileModificationRegistry().incrementFileVersion(confFile);
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
	
	public MemoizedFile getConfFile()
	{
		return confFile;
	}
	
	public boolean fileExists()
	{
		return getConfFile().isFile();
	}
	
	public void verifyAndAutoWrite() throws ConfigException
	{
		verify();
		if (autoWrite) {
			write();
		}
	}
	
	public void verify() throws ConfigException
	{
		getConf().verify();
	}
	
	public void setAutoWrite(boolean autoWrite) {
		this.autoWrite = autoWrite;
	}
	
}
