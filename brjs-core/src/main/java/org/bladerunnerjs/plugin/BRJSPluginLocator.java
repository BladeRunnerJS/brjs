package org.bladerunnerjs.plugin;

import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.plugin.bundler.VirtualProxyBundlerPlugin;
import org.bladerunnerjs.plugin.command.CommandPlugin;
import org.bladerunnerjs.plugin.command.VirtualProxyCommandPlugin;
import org.bladerunnerjs.plugin.content.ContentPlugin;
import org.bladerunnerjs.plugin.content.VirtualProxyContentPlugin;
import org.bladerunnerjs.plugin.minifier.MinifierPlugin;
import org.bladerunnerjs.plugin.minifier.VirtualProxyMinifierPlugin;
import org.bladerunnerjs.plugin.taghandler.TagHandlerPlugin;
import org.bladerunnerjs.plugin.taghandler.VirtualProxyTagHandlerPlugin;


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
		observerPlugins = PluginLoader.createPluginsOfType(brjs, ModelObserverPlugin.class, VirtualProxyModelObserverPlugin.class);
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
	public List<ModelObserverPlugin> getModelObserverPlugins()
	{
		return observerPlugins;
	}
	
	@Override
	public List<MinifierPlugin> getMinifierPlugins() {
		return minifierPlugins;
	}
	
	@Override
	public List<ContentPlugin> getContentPlugins() {
		return contentPlugins;
	}
	
	@Override
	public List<TagHandlerPlugin> getTagHandlerPlugins() {
		return tagHandlerPlugins;
	}
}
