package com.caplin.cutlass.testing;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.ModelObserverPlugin;
import org.bladerunnerjs.plugin.PluginLoader;
import org.bladerunnerjs.plugin.PluginLocator;
import org.bladerunnerjs.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.plugin.command.CommandPlugin;
import org.bladerunnerjs.plugin.command.VirtualProxyCommandPlugin;
import org.bladerunnerjs.plugin.content.ContentPlugin;
import org.bladerunnerjs.plugin.minifier.MinifierPlugin;
import org.bladerunnerjs.plugin.taghandler.TagHandlerPlugin;

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
	public List<ModelObserverPlugin> getModelObserverPlugins() {
		return new ArrayList<>();
	}
	
	@Override
	public List<BundlerPlugin> getBundlerPlugins() {
		return new ArrayList<>();
	}
	
	@Override
	public List<MinifierPlugin> getMinifierPlugins() {
		return new ArrayList<>();
	}
	
	@Override
	public List<ContentPlugin> getContentPlugins() {
		return new ArrayList<>();
	}
	
	@Override
	public List<TagHandlerPlugin> getTagHandlerPlugins() {
		return new ArrayList<>();
	}
}
