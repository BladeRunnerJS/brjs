package org.bladerunnerjs.model.conf;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.exception.ConfigException;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlReader.YamlReaderException;

public class ConfFactory {
	public static <CF extends AbstractYamlConfFile> CF createConfFile(Node node, Class<CF> confClass, File confFile) throws ConfigException {
		CF conf = null;
		
		if(confFile.exists()) {
			conf = readConf(confFile, confClass);
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
	
	private static <CF extends AbstractYamlConfFile> CF readConf(File confFile, Class<CF> confClass) throws ConfigException
	{
		CF conf;
		
		try {
			YamlReader reader = null;
			
			try(FileReader fileReader = new FileReader(confFile)) {
				if(!fileReader.ready()) {
					throw new ConfigException("'" + confFile.getPath() + "' is empty");
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
