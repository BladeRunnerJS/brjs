package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.bladerunnerjs.core.plugin.Plugin;

public interface TagHandlerPlugin extends Plugin {
	String getTagName();
	void writeTagContent(List<String> bundlerRequestPaths, Writer writer) throws IOException;
}
