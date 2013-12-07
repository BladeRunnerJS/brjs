package org.bladerunnerjs.plugin.minifier;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.bladerunnerjs.plugin.Plugin;

public interface MinifierPlugin extends Plugin {
	List<String> getSettingNames();
	void minify(String settingName, List<InputSource> inputSources, Writer writer) throws IOException;
	void generateSourceMap(String minifierLevel, List<InputSource> inputSources, Writer writer) throws IOException;
}
