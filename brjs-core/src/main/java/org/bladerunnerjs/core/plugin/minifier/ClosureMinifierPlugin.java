package org.bladerunnerjs.core.plugin.minifier;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class ClosureMinifierPlugin implements MinifierPlugin {
	private List<String> settingNames = new ArrayList<>();
	
	{
		// TODO: start discovering these plug-ins automatically
		settingNames.add("closure-whitespace");
		settingNames.add("closure-simple");
		settingNames.add("closure-advanced");
	}
	
	@Override
	public List<String> getSettingNames() {
		return settingNames;
	}
	
	@Override
	public void minify(String settingName, List<InputSource> inputSources, Writer writer) throws IOException {
		// TODO: implement this method
	}
	
	@Override
	public void generateSourceMap(String minifierLevel, List<InputSource> inputSources, Writer writer) throws IOException {
		// TODO: implement this method
	}
}
