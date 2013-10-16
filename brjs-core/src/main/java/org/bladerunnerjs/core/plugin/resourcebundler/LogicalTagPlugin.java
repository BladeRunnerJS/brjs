package org.bladerunnerjs.core.plugin.resourcebundler;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.core.plugin.Plugin;
import org.bladerunnerjs.core.plugin.resourcebundler.js.IndexPageFile;


public interface LogicalTagPlugin extends Plugin {
	String getTagName();
	void writeDevTag(Map<String, String> tagAttributes, IndexPageFile indexPageFile, String locale, Writer writer) throws IOException;
	void writeProdTag(Map<String, String> tagAttributes, IndexPageFile indexPageFile, String locale, Writer writer) throws IOException;
}
