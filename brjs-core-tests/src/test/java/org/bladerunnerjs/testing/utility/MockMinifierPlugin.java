package org.bladerunnerjs.testing.utility;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.plugin.InputSource;
import org.bladerunnerjs.plugin.MinifierPlugin;
import org.bladerunnerjs.plugin.base.AbstractMinifierPlugin;


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
	public Reader minify(String settingName, List<InputSource> inputSources) throws ContentProcessingException
	{
		return new StringReader("");
	}

	@Override
	public Reader generateSourceMap(String minifierLevel, List<InputSource> inputSources) throws ContentProcessingException
	{
		return new StringReader("");
	}

}
