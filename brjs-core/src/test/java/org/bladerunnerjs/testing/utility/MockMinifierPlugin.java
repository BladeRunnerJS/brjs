package org.bladerunnerjs.testing.utility;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.core.plugin.minifier.AbstractMinifierPlugin;
import org.bladerunnerjs.core.plugin.minifier.InputSource;
import org.bladerunnerjs.core.plugin.minifier.MinifierPlugin;
import org.bladerunnerjs.model.BRJS;


public class MockMinifierPlugin extends AbstractMinifierPlugin implements MinifierPlugin
{

	@Override
	public void setBRJS(BRJS brjs)
	{
	}

	@Override
	public List<String> getSettingNames()
	{
		return Arrays.asList();
	}

	@Override
	public void minify(String settingName, List<InputSource> inputSources, Writer writer) throws IOException
	{
	}

	@Override
	public void generateSourceMap(String minifierLevel, List<InputSource> inputSources, Writer writer) throws IOException
	{
	}

}
