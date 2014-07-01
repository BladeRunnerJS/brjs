package org.bladerunnerjs.plugin.proxy;

import java.io.Reader;
import java.util.List;

import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.plugin.InputSource;
import org.bladerunnerjs.plugin.MinifierPlugin;

public class VirtualProxyMinifierPlugin extends VirtualProxyPlugin implements MinifierPlugin {
	private MinifierPlugin minifierPlugin;
	
	public VirtualProxyMinifierPlugin(MinifierPlugin minifierPlugin) {
		super(minifierPlugin);
		this.minifierPlugin = minifierPlugin;
	}
	
	@Override
	public List<String> getSettingNames() {
		return minifierPlugin.getSettingNames();
	}
	
	@Override
	public Reader minify(String settingName, List<InputSource> inputSources) throws ContentProcessingException {
		initializePlugin();
		return minifierPlugin.minify(settingName, inputSources);
	}
	
	@Override
	public Reader generateSourceMap(String minifierLevel, List<InputSource> inputSources) throws ContentProcessingException {
		initializePlugin();
		return minifierPlugin.generateSourceMap(minifierLevel, inputSources);
	}
}
