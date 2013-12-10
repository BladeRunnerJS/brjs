package org.bladerunnerjs.testing.utility;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.plugin.BundlerTagHandlerPlugin;
import org.bladerunnerjs.plugin.CommandPlugin;
import org.bladerunnerjs.plugin.BundlerContentPlugin;
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
	public List<CommandPlugin> pluginCommands = new ArrayList<CommandPlugin>();
	public List<ModelObserverPlugin> modelObservers = new ArrayList<ModelObserverPlugin>();
	public List<MinifierPlugin> minifiers = new ArrayList<MinifierPlugin>();
	public List<ContentPlugin> contentPlugins = new ArrayList<ContentPlugin>();
	public List<BundlerContentPlugin> bundlerContentPlugins = new ArrayList<BundlerContentPlugin>();
	public List<TagHandlerPlugin> tagHandlers = new ArrayList<TagHandlerPlugin>();
	public List<BundlerTagHandlerPlugin> bundlerTagHandlers = new ArrayList<BundlerTagHandlerPlugin>();
	public List<AssetPlugin> assetPlugins = new ArrayList<AssetPlugin>();
	
	public void createPlugins(BRJS brjs) {
		setBRJSForPlugins(brjs, pluginCommands);
		setBRJSForPlugins(brjs, modelObservers);
		setBRJSForPlugins(brjs, minifiers);
		setBRJSForPlugins(brjs, contentPlugins);
		setBRJSForPlugins(brjs, bundlerContentPlugins);
		setBRJSForPlugins(brjs, tagHandlers);
		setBRJSForPlugins(brjs, bundlerTagHandlers);
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
	public List<BundlerContentPlugin> getBundlerContentPlugins() {
		return bundlerContentPlugins;
	}
	
	@Override
	public List<TagHandlerPlugin> getTagHandlerPlugins() {
		return tagHandlers;
	}
	
	@Override
	public List<BundlerTagHandlerPlugin> getBundlerTagHandlerPlugins() {
		return bundlerTagHandlers;
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
