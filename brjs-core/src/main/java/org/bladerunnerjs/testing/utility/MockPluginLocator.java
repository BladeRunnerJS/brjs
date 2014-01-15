package org.bladerunnerjs.testing.utility;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.plugin.CommandPlugin;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.MinifierPlugin;
import org.bladerunnerjs.plugin.ModelObserverPlugin;
import org.bladerunnerjs.plugin.Plugin;
import org.bladerunnerjs.plugin.PluginLocator;
import org.bladerunnerjs.plugin.TagHandlerPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyPlugin;
import org.bladerunnerjs.plugin.utility.PluginLocatorUtils;


public class MockPluginLocator implements PluginLocator
{
	public List<CommandPlugin> pluginCommands = new ArrayList<>();
	public List<ModelObserverPlugin> modelObservers = new ArrayList<>();
	public List<MinifierPlugin> minifiers = new ArrayList<>();
	public List<ContentPlugin> contentPlugins = new ArrayList<>();
	public List<TagHandlerPlugin> tagHandlers = new ArrayList<>();
	public List<AssetPlugin> assetPlugins = new ArrayList<>();
	
	public void createPlugins(BRJS brjs) {
		setBRJSForPlugins(brjs, pluginCommands);
		setBRJSForPlugins(brjs, modelObservers);
		setBRJSForPlugins(brjs, minifiers);
		setBRJSForPlugins(brjs, contentPlugins);
		setBRJSForPlugins(brjs, tagHandlers);
		setBRJSForPlugins(brjs, assetPlugins);
	}
	
	@Override
	public List<CommandPlugin> getCommandPlugins()
	{
		return pluginCommands;
	}
	
	@Override
	public List<ModelObserverPlugin> getModelObserverPlugins()
	{
		return modelObservers;
	}
	
	@Override
	public List<MinifierPlugin> getMinifierPlugins() {
		return minifiers;
	}
	
	@Override
	public List<ContentPlugin> getContentPlugins() {
		return contentPlugins;
	}
	
	@Override
	public List<TagHandlerPlugin> getTagHandlerPlugins() {
		return tagHandlers;
	}
	
	@Override
	public List<AssetPlugin> getAssetPlugins() {
		return assetPlugins;
	}
	
	public static List<? extends Plugin> setBRJSForPlugins(BRJS brjs, List<? extends Plugin> plugins)
	{
		for (Plugin p : plugins)
		{
			if ( !(p instanceof VirtualProxyPlugin) ) 
			{ 
				fail("plugin class " + p.getClass() + " wasn't wrapped in a VirtualProxy plugin");
			}
		}
		PluginLocatorUtils.setBRJSForPlugins(brjs, plugins);
		return plugins;
	}
}
