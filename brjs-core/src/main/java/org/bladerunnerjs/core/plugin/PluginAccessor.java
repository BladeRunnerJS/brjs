package org.bladerunnerjs.core.plugin;

import java.util.List;

import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.bundler.VirtualProxyBundlerPlugin;
import org.bladerunnerjs.core.plugin.command.CommandPlugin;
import org.bladerunnerjs.core.plugin.command.VirtualProxyCommandPlugin;
import org.bladerunnerjs.model.BRJS;



public class PluginAccessor implements PluginLocator
{

	private PluginLoader<BundlerPlugin> bundlerPluginLocator = new PluginLoader<BundlerPlugin>();
	private PluginLoader<CommandPlugin> commandPluginLocator = new PluginLoader<CommandPlugin>();
	private PluginLoader<ModelObserverPlugin> modelObserverLocator = new PluginLoader<ModelObserverPlugin>();
	
	@Override
	public List<BundlerPlugin> createBundlerPlugins(BRJS brjs)
	{
		List<BundlerPlugin> plugins = bundlerPluginLocator.createPluginsOfType(brjs, BundlerPlugin.class, VirtualProxyBundlerPlugin.class);
		return plugins;
	}

	@Override
	public List<CommandPlugin> createCommandPlugins(BRJS brjs)
	{
		List<CommandPlugin> plugins = commandPluginLocator.createPluginsOfType(brjs, CommandPlugin.class, VirtualProxyCommandPlugin.class);
		return plugins;
	}
	
	@Override
	public List<ModelObserverPlugin> createModelObservers(BRJS brjs)
	{
		List<ModelObserverPlugin> plugins = modelObserverLocator.createPluginsOfType(brjs, ModelObserverPlugin.class);
		return plugins;
	}
	
}
