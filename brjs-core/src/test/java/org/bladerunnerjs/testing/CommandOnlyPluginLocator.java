package org.bladerunnerjs.testing;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.core.plugin.BRJSPluginLocator;
import org.bladerunnerjs.core.plugin.ModelObserverPlugin;
import org.bladerunnerjs.core.plugin.PluginLocator;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.command.CommandPlugin;
import org.bladerunnerjs.core.plugin.minifier.MinifierPlugin;
import org.bladerunnerjs.core.plugin.servlet.ServletPlugin;
import org.bladerunnerjs.core.plugin.taghandler.TagHandlerPlugin;
import org.bladerunnerjs.model.BRJS;

public class CommandOnlyPluginLocator implements PluginLocator {
	private PluginLocator brjsPluginLocator = new BRJSPluginLocator();
	
	@Override
	public void createPlugins(BRJS brjs) {
		brjsPluginLocator.createPlugins(brjs);
	}
	
	@Override
	public List<CommandPlugin> getCommandPlugins() {
		return brjsPluginLocator.getCommandPlugins();
	}
	
	@Override
	public List<ModelObserverPlugin> getModelObservers() {
		return new ArrayList<>();
	}
	
	@Override
	public List<BundlerPlugin> getBundlerPlugins() {
		return new ArrayList<>();
	}
	
	@Override
	public List<MinifierPlugin> getMinifiers() {
		return new ArrayList<>();
	}
	
	@Override
	public List<ServletPlugin> getServlets() {
		return new ArrayList<>();
	}
	
	@Override
	public List<TagHandlerPlugin> getTagHandlers() {
		return new ArrayList<>();
	}
}
