package org.bladerunnerjs.testing.utility;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.core.plugin.ModelObserverPlugin;
import org.bladerunnerjs.core.plugin.Plugin;
import org.bladerunnerjs.core.plugin.PluginLocator;
import org.bladerunnerjs.core.plugin.PluginLocatorUtils;
import org.bladerunnerjs.core.plugin.VirtualProxyPlugin;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.command.CommandPlugin;
import org.bladerunnerjs.core.plugin.content.ContentPlugin;
import org.bladerunnerjs.core.plugin.minifier.MinifierPlugin;
import org.bladerunnerjs.core.plugin.taghandler.TagHandlerPlugin;
import org.bladerunnerjs.model.BRJS;


public class MockPluginLocator implements PluginLocator
{
	public List<BundlerPlugin> bundlers = new ArrayList<BundlerPlugin>();
	public List<CommandPlugin> pluginCommands = new ArrayList<CommandPlugin>();
	public List<ModelObserverPlugin> modelObservers = new ArrayList<ModelObserverPlugin>();
	public List<MinifierPlugin> minifiers = new ArrayList<MinifierPlugin>();
	public List<ContentPlugin> contentPlugins = new ArrayList<ContentPlugin>();
	public List<TagHandlerPlugin> tagHandlers = new ArrayList<TagHandlerPlugin>();
	
	public void createPlugins(BRJS brjs) {
		setBRJSForPlugins(brjs, bundlers);
		setBRJSForPlugins(brjs, pluginCommands);
		setBRJSForPlugins(brjs, modelObservers);
		setBRJSForPlugins(brjs, minifiers);
		setBRJSForPlugins(brjs, contentPlugins);
		setBRJSForPlugins(brjs, tagHandlers);
	}
	
	@Override
	public List<BundlerPlugin> getBundlerPlugins()
	{
		return bundlers;
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
