package org.bladerunnerjs.yaml;

import java.io.IOException;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.exception.ConfigException;


public interface YamlConfFile {
	MemoizedFile getUnderlyingFile();
	String getRenderedConfig();
	void write() throws ConfigException, IOException;
}
