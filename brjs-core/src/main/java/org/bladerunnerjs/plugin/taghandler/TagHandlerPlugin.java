package org.bladerunnerjs.plugin.taghandler;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.plugin.Plugin;

public interface TagHandlerPlugin extends Plugin {
	String getTagName();
	void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException;
	void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException;
}
