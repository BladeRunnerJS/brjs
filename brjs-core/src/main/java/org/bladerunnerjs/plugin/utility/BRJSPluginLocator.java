package org.bladerunnerjs.plugin.utility;

import java.util.List;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.plugin.AssetLocationPlugin;
import org.bladerunnerjs.api.plugin.AssetPlugin;
import org.bladerunnerjs.api.plugin.CommandPlugin;
import org.bladerunnerjs.api.plugin.ContentPlugin;
import org.bladerunnerjs.api.plugin.MinifierPlugin;
import org.bladerunnerjs.api.plugin.ModelObserverPlugin;
import org.bladerunnerjs.api.plugin.PluginLocator;
import org.bladerunnerjs.api.plugin.RequirePlugin;
import org.bladerunnerjs.api.plugin.TagHandlerPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyAssetLocationPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyAssetPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyCommandPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyContentPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyMinifierPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyModelObserverPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyRequirePlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyTagHandlerPlugin;


public class BRJSPluginLocator implements PluginLocator
{
	private List<ModelObserverPlugin> modelObserverPlugins;
	private List<CommandPlugin> commandPlugins;
	private List<MinifierPlugin> minifierPlugins;
	private List<ContentPlugin> contentPlugins;
	private List<TagHandlerPlugin> tagHandlerPlugins;
	private List<AssetPlugin> assetPlugins;
	private List<AssetLocationPlugin> assetLocationPlugins;
	private List<RequirePlugin> requirePlugins;
	
	@Override
	public void createPlugins(BRJS brjs) {
		modelObserverPlugins = PluginLoader.createPluginsOfType(brjs, ModelObserverPlugin.class, VirtualProxyModelObserverPlugin.class);
		commandPlugins = PluginLoader.createPluginsOfType(brjs, CommandPlugin.class, VirtualProxyCommandPlugin.class);
		minifierPlugins = PluginLoader.createPluginsOfType(brjs, MinifierPlugin.class, VirtualProxyMinifierPlugin.class);
		contentPlugins = PluginLoader.createPluginsOfType(brjs, ContentPlugin.class, VirtualProxyContentPlugin.class);
		tagHandlerPlugins = PluginLoader.createPluginsOfType(brjs, TagHandlerPlugin.class, VirtualProxyTagHandlerPlugin.class);
		assetPlugins = PluginLoader.createPluginsOfType(brjs, AssetPlugin.class, VirtualProxyAssetPlugin.class);
		assetLocationPlugins = PluginLoader.createPluginsOfType(brjs, AssetLocationPlugin.class, VirtualProxyAssetLocationPlugin.class);
		requirePlugins = PluginLoader.createPluginsOfType(brjs, RequirePlugin.class, VirtualProxyRequirePlugin.class);
	}

	@Override
	public List<CommandPlugin> getCommandPlugins()
	{
		return commandPlugins;
	}
	
	@Override
	public List<ModelObserverPlugin> getModelObserverPlugins()
	{
		return modelObserverPlugins;
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
	
	@Override
	public List<AssetPlugin> getAssetPlugins() {
		return assetPlugins;
	}
	
	@Override
	public List<AssetLocationPlugin> getAssetLocationPlugins() {
		return assetLocationPlugins;
	}

	@Override
	public List<RequirePlugin> getRequirePlugins() {
		return requirePlugins;
	}
}
