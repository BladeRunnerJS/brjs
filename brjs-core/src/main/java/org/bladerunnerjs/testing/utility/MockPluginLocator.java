package org.bladerunnerjs.testing.utility;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.core.plugin.ModelObserverPlugin;
import org.bladerunnerjs.core.plugin.PluginLocator;
import org.bladerunnerjs.core.plugin.PluginLocatorUtils;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.command.CommandPlugin;
import org.bladerunnerjs.model.BRJS;


public class MockPluginLocator implements PluginLocator
{
	public List<BundlerPlugin> bundlers = new ArrayList<BundlerPlugin>();
	public List<CommandPlugin> pluginCommands = new ArrayList<CommandPlugin>();
	public List<ModelObserverPlugin> modelObservers = new ArrayList<ModelObserverPlugin>();

	@Override
	public List<BundlerPlugin> createBundlerPlugins(BRJS brjs)
	{
		PluginLocatorUtils.setBRJSForPlugins(brjs, bundlers);
		return bundlers;
	}

	@Override
	public List<CommandPlugin> createCommandPlugins(BRJS brjs)
	{
		PluginLocatorUtils.setBRJSForPlugins(brjs, pluginCommands);
		return pluginCommands;
	}

	@Override
	public List<ModelObserverPlugin> createModelObservers(BRJS brjs)
	{
		PluginLocatorUtils.setBRJSForPlugins(brjs, modelObservers);
		return modelObservers;
	}

}
