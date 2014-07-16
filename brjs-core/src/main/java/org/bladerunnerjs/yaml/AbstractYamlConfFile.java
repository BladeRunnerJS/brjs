package org.bladerunnerjs.yaml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.utility.EncodedFileUtil;

import com.esotericsoftware.yamlbeans.YamlWriter;
import com.google.common.base.Joiner;

public abstract class AbstractYamlConfFile implements YamlConfFile {
	protected BRJSNode node;
	private File confFile;
	private EncodedFileUtil fileUtil;
	
	public void setNode(BRJSNode node) {
		this.node = node;
	}
	
	public void setConfFile(File confFile) {
		this.confFile = confFile;
		
		try {
			// TODO: get rid of `node == null` guard once we delete no brjs-core code
			String fileEncoding = ((node == null) || confFile.getName().equals("brjs.conf")) ? "UTF-8" : node.root().bladerunnerConf().getDefaultFileCharacterEncoding();
			fileUtil = new EncodedFileUtil(fileEncoding);
		}
		catch(ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	public abstract void initialize(BRJSNode node);
	public abstract void verify() throws ConfigException;
	
	@Override
	public File getUnderlyingFile() {
		return confFile;
	}
	
	@Override
	public String getRenderedConfig() {
		List<String> lines;
		
		try {
			try(ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
				YamlWriter writer = new YamlWriter(new PrintWriter(byteStream));
				writer.write(this);
				writer.close();
				
				// YamlWriter writes out the classname at the top of the file which we dont want
				lines = IOUtils.readLines(new ByteArrayInputStream(byteStream.toByteArray()));
				lines.remove(0);
			}
		}
		catch(IOException e) {
			// given we're using byte array, this will never happen anyway
			throw new RuntimeException(e);
		}
		
		return Joiner.on("\n").join(lines);
	}
	
	@Override
	public void write() throws ConfigException, IOException {
		verify();
		
		fileUtil.write(confFile, getRenderedConfig());
	}
	
	
	public <T extends Object> T getDefault(T currentValue, T defaultValue)
	{
		boolean useDefaultValue = currentValue == null || currentValue.equals(0);
		
		if (useDefaultValue)
		{
			return defaultValue;
		}
		return currentValue;
	}
	
}
