package com.caplin.cutlass.testing;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.core.plugin.ModelObserverPlugin;
import org.bladerunnerjs.core.plugin.PluginLoader;
import org.bladerunnerjs.core.plugin.PluginLocator;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.command.CommandPlugin;
import org.bladerunnerjs.core.plugin.command.VirtualProxyCommandPlugin;
import org.bladerunnerjs.core.plugin.minifier.MinifierPlugin;
import org.bladerunnerjs.core.plugin.servlet.ContentPlugin;
import org.bladerunnerjs.core.plugin.taghandler.TagHandlerPlugin;
import org.bladerunnerjs.model.BRJS;

public class CommandOnlyPluginLocator implements PluginLocator {
	private List<CommandPlugin> commandPlugins;
	
	@Override
	public void createPlugins(BRJS brjs) {
		commandPlugins = PluginLoader.createPluginsOfType(brjs, CommandPlugin.class, VirtualProxyCommandPlugin.class);
	}
	
	@Override
	public List<CommandPlugin> getCommandPlugins() {
		return commandPlugins;
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
	public List<ContentPlugin> getContentPlugins() {
		return new ArrayList<>();
	}
	
	@Override
	public List<TagHandlerPlugin> getTagHandlers() {
		return new ArrayList<>();
	}
}
