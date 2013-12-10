package org.bladerunnerjs.plugin.utility;

import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.plugin.BundlerTagHandlerPlugin;
import org.bladerunnerjs.plugin.CommandPlugin;
import org.bladerunnerjs.plugin.BundlerContentPlugin;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.MinifierPlugin;
import org.bladerunnerjs.plugin.ModelObserverPlugin;
import org.bladerunnerjs.plugin.PluginLocator;
import org.bladerunnerjs.plugin.TagHandlerPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyAssetPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyBundlerTagHandlerPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyCommandPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyBundlerContentPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyContentPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyMinifierPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyModelObserverPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyTagHandlerPlugin;


public class BRJSPluginLocator implements PluginLocator
{
	private List<ModelObserverPlugin> modelObserverPlugins;
	private List<CommandPlugin> commandPlugins;
	private List<MinifierPlugin> minifierPlugins;
	private List<ContentPlugin> contentPlugins;
	private List<BundlerContentPlugin> bundlerContentPlugins;
	private List<TagHandlerPlugin> tagHandlerPlugins;
	private List<BundlerTagHandlerPlugin> bundlerTagHandlerPlugins;
	private List<AssetPlugin> assetPlugins;
	
	@Override
	public void createPlugins(BRJS brjs) {
		modelObserverPlugins = PluginLoader.createPluginsOfType(brjs, ModelObserverPlugin.class, VirtualProxyModelObserverPlugin.class);
		commandPlugins = PluginLoader.createPluginsOfType(brjs, CommandPlugin.class, VirtualProxyCommandPlugin.class);
		minifierPlugins = PluginLoader.createPluginsOfType(brjs, MinifierPlugin.class, VirtualProxyMinifierPlugin.class);
		contentPlugins = PluginLoader.createPluginsOfType(brjs, ContentPlugin.class, VirtualProxyContentPlugin.class);
		bundlerContentPlugins = PluginLoader.createPluginsOfType(brjs, BundlerContentPlugin.class, VirtualProxyBundlerContentPlugin.class);
		tagHandlerPlugins = PluginLoader.createPluginsOfType(brjs, TagHandlerPlugin.class, VirtualProxyTagHandlerPlugin.class);
		bundlerTagHandlerPlugins = PluginLoader.createPluginsOfType(brjs, BundlerTagHandlerPlugin.class, VirtualProxyBundlerTagHandlerPlugin.class);
		assetPlugins = PluginLoader.createPluginsOfType(brjs, AssetPlugin.class, VirtualProxyAssetPlugin.class);
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
	public List<BundlerContentPlugin> getBundlerContentPlugins() {
		return bundlerContentPlugins;
	}
	
	@Override
	public List<TagHandlerPlugin> getTagHandlerPlugins() {
		return tagHandlerPlugins;
	}
	
	@Override
	public List<BundlerTagHandlerPlugin> getBundlerTagHandlerPlugins() {
		return bundlerTagHandlerPlugins;
	}
	
	@Override
	public List<AssetPlugin> getAssetPlugins() {
		return assetPlugins;
	}
}
