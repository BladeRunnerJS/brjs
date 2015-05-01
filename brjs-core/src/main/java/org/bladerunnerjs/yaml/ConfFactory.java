package org.bladerunnerjs.yaml;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.api.BRJSNode;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.utility.UnicodeReader;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlReader.YamlReaderException;

public class ConfFactory {
	public static <CF extends AbstractYamlConfFile> CF createConfFile(BRJSNode node, Class<CF> confClass, MemoizedFile confFile, String defaultFileCharacterEncoding) throws ConfigException {
		CF conf = null;
		
		if(confFile.exists()) {
			conf = readConf(node, confFile, confClass, defaultFileCharacterEncoding);
		}
		else {
			conf = newConf(node, confClass);
		}
		
		conf.setNode(node);
		conf.setConfFile(confFile);
		conf.verify();
		
		return conf;
	}
	
	private static <CF extends AbstractYamlConfFile>  CF newConf(BRJSNode node, Class<CF> confClass)
	{
		CF conf = null;
		
		try {
			conf = confClass.newInstance();
			conf.initialize(node);
		}
		catch(InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return conf;
	}
	
	private static <CF extends AbstractYamlConfFile> CF readConf(BRJSNode node, MemoizedFile confFile, Class<CF> confClass, String defaultFileCharacterEncoding) throws ConfigException
	{
		CF conf;
		
		try {
			YamlReader reader = null;
			
			try(Reader fileReader = new UnicodeReader(confFile, defaultFileCharacterEncoding)) {
				if(!fileReader.ready()) {
					return newConf(node, confClass);
				}
				
				reader = new YamlReader(fileReader);
				conf = reader.read(confClass);
				conf.initialize(node);
			}
			finally {
				if(reader != null) {
					reader.close();
				}
			}
		}
		catch(YamlReaderException e) {
			throw new ConfigException("Parse error while reading\n '" + confFile.getPath() + "':\n\n" + 
										e.getMessage() + "\n\n" +
										"Please check to see that your properties have a space after the colon ':' as this is a common issue encounterd with YAML files.");
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
		
		return conf;
	}
}
