package org.bladerunnerjs.core.plugin.minifier;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;

public class ConcatentatingMinifierPlugin extends AbstractMinifierPlugin implements MinifierPlugin {
	private List<String> settingNames = new ArrayList<>();
	
	{
		settingNames.add("combined");
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}
	
	@Override
	public List<String> getSettingNames() {
		return settingNames;
	}
	
	@Override
	public void minify(String settingName, List<InputSource> inputSources, Writer writer) throws IOException {
		for(InputSource inputSource : inputSources) {
			writer.write(inputSource.getSource());
			writer.write("\n\n");
		}
	}
	
	@Override
	public void generateSourceMap(String minifierLevel, List<InputSource> inputSources, Writer writer) throws IOException {
		throw new RuntimeException("The ConcatentatingMinifierPlugin does not support souce-maps, so should never receive a request to create one.");
	}
}
