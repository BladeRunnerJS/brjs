package org.bladerunnerjs.model.conf;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.model.exception.ConfigException;


public interface YamlConfFile {
	File getUnderlyingFile();
	String getRenderedConfig();
	void write() throws ConfigException, IOException;
}
