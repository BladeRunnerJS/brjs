package org.bladerunnerjs.core.plugin.minifier;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public interface MinifierPlugin {
	List<String> getSettingNames();
	void minify(String settingName, List<InputSource> inputSources, Writer writer) throws IOException;
	void generateSourceMap(String minifierLevel, List<InputSource> inputSources, Writer writer) throws IOException;
}
