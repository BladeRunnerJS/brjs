package org.bladerunnerjs.yaml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.exception.ConfigException;

import com.esotericsoftware.yamlbeans.YamlWriter;
import com.google.common.base.Joiner;

public abstract class AbstractYamlConfFile implements YamlConfFile {
	protected BRJSNode node;
	private File confFile;
	
	public void setNode(BRJSNode node) {
		this.node = node;
	}
	
	public void setConfFile(File confFile) {
		this.confFile = confFile;
	}
	
	public abstract void initialize();
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
		
		// TODO: remove this line and just use getDefaultInputEncoding() directly once non brjs-core code has been deleted, as otherwise `node` is never null
		String fileEncoding = (node != null) ? node.root().bladerunnerConf().getDefaultInputEncoding() : "UTF-8";
		FileUtils.write(confFile, getRenderedConfig(), fileEncoding);
	}
	
}
