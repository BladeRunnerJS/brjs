package org.bladerunnerjs.core.plugin;

import java.util.List;

import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.bundler.VirtualProxyBundlerPlugin;
import org.bladerunnerjs.core.plugin.command.CommandPlugin;
import org.bladerunnerjs.core.plugin.command.VirtualProxyCommandPlugin;
import org.bladerunnerjs.core.plugin.minifier.MinifierPlugin;
import org.bladerunnerjs.core.plugin.minifier.VirtualProxyMinifierPlugin;
import org.bladerunnerjs.core.plugin.servlet.ServletPlugin;
import org.bladerunnerjs.core.plugin.servlet.VirtualProxyServletPlugin;
import org.bladerunnerjs.core.plugin.taghandler.TagHandlerPlugin;
import org.bladerunnerjs.core.plugin.taghandler.VirtualProxyTagHandlerPlugin;
import org.bladerunnerjs.model.BRJS;



public class BRJSPluginLocator implements PluginLocator
{
	private List<ModelObserverPlugin> observerPlugins;
	private List<BundlerPlugin> bundlerPlugins;
	private List<CommandPlugin> commandPlugins;
	private List<MinifierPlugin> minifierPlugins;
	private List<ServletPlugin> servletPLugins;
	private List<TagHandlerPlugin> tagHandlerPlugins;
	
	
	@Override
	public void createPlugins(BRJS brjs) {
		observerPlugins = PluginLoader.createPluginsOfType(brjs, ModelObserverPlugin.class);
		bundlerPlugins = PluginLoader.createPluginsOfType(brjs, BundlerPlugin.class, VirtualProxyBundlerPlugin.class);
		commandPlugins = PluginLoader.createPluginsOfType(brjs, CommandPlugin.class, VirtualProxyCommandPlugin.class);
		minifierPlugins = PluginLoader.createPluginsOfType(brjs, MinifierPlugin.class, VirtualProxyMinifierPlugin.class);
		servletPLugins = PluginLoader.createPluginsOfType(brjs, ServletPlugin.class, VirtualProxyServletPlugin.class);
		tagHandlerPlugins = PluginLoader.createPluginsOfType(brjs, TagHandlerPlugin.class, VirtualProxyTagHandlerPlugin.class);
	}
	
	@Override
	public List<BundlerPlugin> getBundlerPlugins()
	{
		return bundlerPlugins;
	}

	@Override
	public List<CommandPlugin> getCommandPlugins()
	{
		return commandPlugins;
	}
	
	@Override
	public List<ModelObserverPlugin> getModelObservers()
	{
		return observerPlugins;
	}
	
	@Override
	public List<MinifierPlugin> getMinifiers() {
		return minifierPlugins;
	}
	
	@Override
	public List<ServletPlugin> getServlets() {
		return servletPLugins;
	}
	
	@Override
	public List<TagHandlerPlugin> getTagHandlers() {
		return tagHandlerPlugins;
	}
}
