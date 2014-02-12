package org.bladerunnerjs.yaml;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.utility.UnicodeReader;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlReader.YamlReaderException;

public class ConfFactory {
	public static <CF extends AbstractYamlConfFile> CF createConfFile(BRJSNode node, Class<CF> confClass, File confFile) throws ConfigException {
		CF conf = null;
		// TODO: get rid of `node == null` guard once we delete no brjs-core code
		String defaultInputEncoding = ((node == null) || confFile.getName().equals("bladerunner.conf")) ? "UTF-8" : node.root().bladerunnerConf().getDefaultInputEncoding();
		
		if(confFile.exists()) {
			conf = readConf(confFile, confClass, defaultInputEncoding);
			conf.setNode(node);
			conf.setConfFile(confFile);
			conf.verify();
		}
		else {
			conf = newConf(confClass);
			conf.setNode(node);
			conf.setConfFile(confFile);
		}
		
		return conf;
	}
	
	private static <CF extends AbstractYamlConfFile>  CF newConf(Class<CF> confClass)
	{
		CF conf = null;
		
		try {
			conf = confClass.newInstance();
			conf.initialize();
		}
		catch(InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return conf;
	}
	
	private static <CF extends AbstractYamlConfFile> CF readConf(File confFile, Class<CF> confClass, String defaultInputEncoding) throws ConfigException
	{
		CF conf;
		
		try {
			YamlReader reader = null;
			
			try(Reader fileReader = new UnicodeReader(confFile, defaultInputEncoding)) {
				if(!fileReader.ready()) {
					throw new ConfigException("'" + confFile.getPath() + "' is empty, either add some configuration or delete it to use the default configuration.");
				}
				
				reader = new YamlReader(fileReader);
				conf = reader.read(confClass);
			}
			finally {
				if(reader != null) {
					reader.close();
				}
			}
		}
		catch(YamlReaderException e) {
			throw new ConfigException("Parse error while reading '" + confFile.getPath() + "':\n" + e.getMessage());
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
		
		return conf;
	}
}
