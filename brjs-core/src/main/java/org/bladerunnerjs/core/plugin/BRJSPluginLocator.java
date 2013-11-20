package org.bladerunnerjs.core.plugin;

import java.util.List;

import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.bundler.VirtualProxyBundlerPlugin;
import org.bladerunnerjs.core.plugin.command.CommandPlugin;
import org.bladerunnerjs.core.plugin.command.VirtualProxyCommandPlugin;
import org.bladerunnerjs.core.plugin.minifier.MinifierPlugin;
import org.bladerunnerjs.core.plugin.minifier.VirtualProxyMinifierPlugin;
import org.bladerunnerjs.core.plugin.servlet.ContentPlugin;
import org.bladerunnerjs.core.plugin.servlet.VirtualProxyContentPlugin;
import org.bladerunnerjs.core.plugin.taghandler.TagHandlerPlugin;
import org.bladerunnerjs.core.plugin.taghandler.VirtualProxyTagHandlerPlugin;
import org.bladerunnerjs.model.BRJS;


public class BRJSPluginLocator implements PluginLocator
{
	private List<ModelObserverPlugin> observerPlugins;
	private List<BundlerPlugin> bundlerPlugins;
	private List<CommandPlugin> commandPlugins;
	private List<MinifierPlugin> minifierPlugins;
	private List<ContentPlugin> contentPlugins;
	private List<TagHandlerPlugin> tagHandlerPlugins;
	
	
	@Override
	public void createPlugins(BRJS brjs) {
		observerPlugins = PluginLoader.createPluginsOfType(brjs, ModelObserverPlugin.class);
		bundlerPlugins = PluginLoader.createPluginsOfType(brjs, BundlerPlugin.class, VirtualProxyBundlerPlugin.class);
		commandPlugins = PluginLoader.createPluginsOfType(brjs, CommandPlugin.class, VirtualProxyCommandPlugin.class);
		minifierPlugins = PluginLoader.createPluginsOfType(brjs, MinifierPlugin.class, VirtualProxyMinifierPlugin.class);
		contentPlugins = PluginLoader.createPluginsOfType(brjs, ContentPlugin.class, VirtualProxyContentPlugin.class);
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
	public List<ContentPlugin> getContentPlugins() {
		return contentPlugins;
	}
	
	@Override
	public List<TagHandlerPlugin> getTagHandlers() {
		return tagHandlerPlugins;
	}
}
