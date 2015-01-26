package org.bladerunnerjs.api.spec.utility;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.plugin.AssetLocationPlugin;
import org.bladerunnerjs.api.plugin.AssetPlugin;
import org.bladerunnerjs.api.plugin.CommandPlugin;
import org.bladerunnerjs.api.plugin.ContentPlugin;
import org.bladerunnerjs.api.plugin.MinifierPlugin;
import org.bladerunnerjs.api.plugin.ModelObserverPlugin;
import org.bladerunnerjs.api.plugin.Plugin;
import org.bladerunnerjs.api.plugin.PluginLocator;
import org.bladerunnerjs.api.plugin.RequirePlugin;
import org.bladerunnerjs.api.plugin.TagHandlerPlugin;
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
	public List<AssetLocationPlugin> assetLocationPlugins = new ArrayList<>();
	public List<RequirePlugin> requirePlugins = new ArrayList<>();
	
	public void createPlugins(BRJS brjs) {
		setBRJSForPlugins(brjs, pluginCommands);
		setBRJSForPlugins(brjs, modelObservers);
		setBRJSForPlugins(brjs, minifiers);
		setBRJSForPlugins(brjs, contentPlugins);
		setBRJSForPlugins(brjs, tagHandlers);
		setBRJSForPlugins(brjs, assetPlugins);
		setBRJSForPlugins(brjs, assetLocationPlugins);
		setBRJSForPlugins(brjs, requirePlugins);
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
	
	@Override
	public List<AssetLocationPlugin> getAssetLocationPlugins() {
		return assetLocationPlugins;
	}
	
	@Override
	public List<RequirePlugin> getRequirePlugins() {
		return requirePlugins;
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
