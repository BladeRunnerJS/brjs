package org.bladerunnerjs.core.plugin.taghandler;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.core.plugin.Plugin;
import org.bladerunnerjs.model.BundleSet;

public interface TagHandlerPlugin extends Plugin {
	String getTagName();
	void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException;
	void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException;
}
