package org.bladerunnerjs.plugin.utility;

import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.BundlerPlugin;
import org.bladerunnerjs.plugin.CommandPlugin;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.MinifierPlugin;
import org.bladerunnerjs.plugin.ModelObserverPlugin;
import org.bladerunnerjs.plugin.PluginLocator;
import org.bladerunnerjs.plugin.TagHandlerPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyBundlerPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyCommandPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyContentPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyMinifierPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyModelObserverPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyTagHandlerPlugin;


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
