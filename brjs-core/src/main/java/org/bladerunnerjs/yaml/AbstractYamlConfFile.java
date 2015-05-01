package org.bladerunnerjs.yaml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.BRJSNode;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.utility.EncodedFileUtil;

import com.esotericsoftware.yamlbeans.YamlWriter;

public abstract class AbstractYamlConfFile implements YamlConfFile {
	
	// see http://stackoverflow.com/questions/5205339/regular-expression-matching-fully-qualified-java-classes
	private static final String JAVA_CLASSNAME_REGEX = "([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*";
	
	protected BRJSNode node;
	private MemoizedFile confFile;
	private EncodedFileUtil fileUtil;
	
	public void setNode(BRJSNode node) {
		this.node = node;
	}
	
	public void setConfFile(File confFile) {
		this.confFile = node.root().getMemoizedFile(confFile);
		
		try {
			// TODO: get rid of `node == null` guard once we delete no brjs-core code
			String fileEncoding = ((node == null) || confFile.getName().equals("brjs.conf")) ? "UTF-8" : node.root().bladerunnerConf().getDefaultFileCharacterEncoding();
			fileUtil = new EncodedFileUtil(node.root(), fileEncoding);
		}
		catch(ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	public abstract void initialize(BRJSNode node);
	public abstract void verify() throws ConfigException;
	
	@Override
	public MemoizedFile getUnderlyingFile() {
		return confFile;
	}
	
	@Override
	public String getRenderedConfig() {
		StringBuilder renderedConfig = new StringBuilder();
		
		try {
			try(ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
				YamlWriter writer = new YamlWriter(new PrintWriter(byteStream));
				writer.write(this);
				writer.close();
				
				List<String> yamlConfLines = Arrays.asList(StringUtils.split(byteStream.toString(), "\n"));
				for (String line : yamlConfLines) {
					// YamlWriter writes out the classnames which we don't want
					line = line.replaceFirst("((^!)|( !))"+JAVA_CLASSNAME_REGEX+"$", "");
					line = line.replaceFirst("\\s+$", ""); // remove end of line whitespace
					if (!line.isEmpty()) {
						renderedConfig.append(line+"\n");
					}
				}
			}
		}
		catch(IOException e) {
			// given we're using byte array, this will never happen anyway
			throw new RuntimeException(e);
		}
		
		return renderedConfig.toString().trim();
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
